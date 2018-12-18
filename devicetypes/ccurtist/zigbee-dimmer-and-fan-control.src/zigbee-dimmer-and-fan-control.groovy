/**
 *	
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
	definition (name: "ZigBee Dimmer and Fan Control", namespace: "ccurtist", author: "Christopher Curti", runLocally: false, minHubCoreVersion: '000.019.00012', executeCommandsLocally: false) {
		capability "Actuator"
		capability "Configuration"
		capability "Refresh"
		capability "Switch"
		capability "Switch Level"
		capability "Health Check"
		capability "Light"
        capability "Fan Speed"
        
        command "fanOn"
        command "fanOff"
        	
        
        attribute "fanSpeed", "number"
        attribute "storedFanSpeed", "number"

		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0202", outClusters: "0003, 0019", model: "HBUniversalCFRemote"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0202", outClusters: "0003, 0019", model: "HDC52EastwindFan"
	}
	preferences {
    	page(name: "remakeChildren", title: "This does not display on DTH preference page")
            section("section") {              
            	input(name: "recreateChildren", type: "bool", title: "Delete & Recreate all child devices?\n\nTypically used after modifying the parent device or changing to a new DTH. " +
                "above to give all child devices the new name.\n\nPLEASE NOTE: You will have to redo any SmartApps using the Child Deevice after running this")                      
       }
    }
	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:"light on", action:"switch.off", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'light off', action:"switch.on", icon:"st.Lighting.light24", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'turning on', action:"switch.off", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'turning off', action:"switch.on", icon:"st.Lighting.light24", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
            	tileAttribute ("fanSpeed", key: "SECONDARY_CONTROL") {
		    	attributeState "00", label:'', action:"fanOn", icon:"http://gdurl.com/6vQm" 
               		attributeState "05", label:'', action:"fanOn", icon:"http://gdurl.com/6vQm" 
                	attributeState "01", label:'', action:"fanOff", icon:"http://gdurl.com/hFV0"
                	attributeState "02", label:'', action:"fanOff", icon:"http://gdurl.com/d9p4"  
                	attributeState "03", label:'', action:"fanOff", icon:"http://gdurl.com/L_sw"
                	attributeState "04", label:'', action:"fanOff", icon:"http://gdurl.com/kO65"
                	attributeState "06", label:'', action:"fanOff", icon:"http://gdurl.com/sN0r"
			}
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        
        	childDeviceTile("fanButton00", "fanButton00", height: 1, width: 1)
        	childDeviceTile("fanButton01", "fanButton01", height: 1, width: 1)
    		childDeviceTile("fanButton02", "fanButton02", height: 1, width: 1)
    		childDeviceTile("fanButton03", "fanButton03", height: 1, width: 1)
    		childDeviceTile("fanButton04", "fanButton04", height: 1, width: 1)
    		childDeviceTile("fanButton06", "fanButton06", height: 1, width: 1)
    	
		main(["switch"])
		details(["switch", "fanButton00", "fanButton01", "fanButton02", "fanButton03", "fanButton04", "fanButton06", "refresh"])
	}
}

def parse(String description) {
	def event = zigbee.getEvent(description) 
		if (event) {
        		log.info "Light Event: ${event}"
			sendEvent(event)
	} else {
		def map = [:]
		def descMap = zigbee.parseDescriptionAsMap(description)
		if (descMap){
			if (description?.startsWith('read attr -')){
				if (descMap.clusterInt == 514 && descMap.attrInt == 0){
					map.name = "fanSpeed"
					map.value = descMap.value
					map.descriptionText = "Fan speed is ${map.value}"
                    if (map.value != NULL){
			    if ((map.value != "00")){setLastOnSpeed(map.value)}
			    sendEvent(name: "fanSpeed", value: map.value, displayed: true, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
			    updateChildSwitches(map.value)
                    	}
					log.debug "parse returned ${map}"
				}
			}
			else if (descMap.clusterInt == 0x0006 && descMap.commandInt == 0x07){
            	if (descMap.data[0] == "00"){
					log.debug "ON/OFF REPORTING CONFIG RESPONSE: " + cluster
					sendEvent(name: "checkInterval", value: 60 * 12, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
                } else {
					log.warn "ON/OFF REPORTING CONFIG FAILED- error code:${cluster.data[0]}"
                }
			}
		}
	}
}

def on(){
	zigbee.on()
}

def off(){
    zigbee.off()	
}

def fanOn() {
	setFanSpeed(device.currentValue("storedFanSpeed"))	
}

def fanOff() {
	setFanSpeed("00")
}

def setLevel(value) {
	if(value != 0){  zigbee.setLevel(value) + zigbee.on()}
    else{ zigbee.setLevel(value)}
}

def setLastOnSpeed(value){
	sendEvent("name": "storedFanSpeed", "value": value)
}

def setFanSpeed(value){
    def intVal= zigbee.convertHexToInt("${value}")
    zigbee.writeAttribute(0x0202, 0x00, 0x30, intVal)
}

def ping() {
	return zigbee.onOffRefresh()
}

def refresh() {
  zigbee.onOffRefresh() + zigbee.levelRefresh() + zigbee.readAttribute(0x0202, 0x0000)
}

def installed() {
	initialize()
	 //to make sure value initially saved
}

def updated() {
	if(state.oldLabel != device.label) {updateChildLabel()}
		initialize()    
}

def configure() {
   sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
   refresh() + zigbee.onOffConfig(0, 300) + zigbee.levelConfig() + zigbee.configureReporting(0x0202, 0x0000, 0x30, 0, 600, null)
}

def updateChildLabel() {
	for(i in 0..6) {   		
    	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    	}                 
        if (childDevice && i != 5) {childDevice.label = "${device.displayName} Mode ${getFanModeName()["0${i}"]}"} // rename with new label
    }
}

def createFanChildren() {
	log.debug "create fan children"
	state.oldLabel = device.label
    for (i in 0..6) {
		def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    		}
    
    	if (!childDevice && i != 5){
    			childDevice = addChildDevice("Zigbee Dimmer and Fan Control Child Button", "${device.deviceNetworkId}-0${i}", null,[completedSetup: true,
            	label: "${device.displayName} Mode ${getFanModeName()["0${i}"]}", isComponent: true, componentName: "fanButton0${i}",
            	componentLabel: "${getFanModeName()[i]}", "data":["speed": "0${i}"]])        	
           		log.info "Creating child fan mode ${childDevice}"
                childDevice.sendEvent(name:"button",value: "${getFanModeName()["0${i}"]}")      ///Can probably get rid of one of these
                childDevice.sendEvent(name:"fanMode",value: "${getFanModeName()["0${i}"]}")     
        } else { log.info "Child already exists"}
    }
}

def deleteChildren() {	
	def children = getChildDevices()        	
    	children.each {child->
  		deleteChildDevice(child.deviceNetworkId)
    }	
    log.info "Deleting children"                  
}

def updateChildSwitches(fan) {
	def fanID = zigbee.convertHexToInt(fan)
	def children = getChildDevices()
   	children.each {child->
    	
       	def childSpeed = child.getDataValue('speed')
        if("${childSpeed}" == "0${fanID}") {
           	child.sendEvent(name:"switch",value:"on")
        }
        else if (child.currentValue("switch") != "off"){ 
        	child.sendEvent(name:"switch",value:"off")	
		}
   	}
}

def initialize() {	
	log.info "Initializing"     
       	if(recreateChildren) {        	
		deleteChildren()            
    		device.updateSetting("recreateChildren", false)
            	runIn(2, "createFanChildren")
         }
	else{
		createFanChildren()
		response(refresh() + configure())
	}
}

def getFanModeName(){
	[
		"00" : "OFF",
		"01" : "LOW",
		"02" : "MED",
		"03" : "MED-HIGH",
		"04" : "HIGH",
		"05" : "OFF",
		"06" : "AUTO"
		]

}