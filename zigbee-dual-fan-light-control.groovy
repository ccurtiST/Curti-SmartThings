/**
 *	Copyright 2015 SmartThings
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
	definition (name: "ZigBee Dimmer and Fan Control", namespace: "ccurtiST", author: "Christopher Curti", runLocally: false, minHubCoreVersion: '000.019.00012', executeCommandsLocally: false) {
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
        command "lightOn"
        command "lightOff"
        command "setLevelBrightness"
        
        attribute "fanSpeed", "number"
        attribute "storedFanSpeed", "number"

		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0202", outClusters: "0003, 0019", model: "HBUniversalCFRemote"
	}

	tiles(scale: 2) {
		/*multiAttributeTile(name:"fanSpeed", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("fanSpeed", key: "PRIMARY_CONTROL") {
				attributeState "00", label:'off', action:"fanOn", icon:"st.Lighting.light24", backgroundColor:"#FFFFFF", nextState:"turningOn"
                attributeState "05", label:'off', action:"fanOn", icon:"st.Lighting.light24", backgroundColor:"#FFFFFF", nextState:"turningOn"
                attributeState "01", label:'low', action:"fanOff", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "02", label:'med', action:"fanOff", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "03", label:'med-high', action:"fanOff", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "04", label:'high', action:"fanOff", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "06", label:'auto', action:"fanOff", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
                attributeState "turningOff", label:'turning Off', action:"fanOn", icon:"st.Lighting.light24", backgroundColor:"#FFFFFF", nextState:"turningOn"
                attributeState "turningOn", label:'turning On', action:"fanOff", icon:"st.Lighting.light24", backgroundColor:"#00A0DC", nextState:"turningOff"
			}*/
           
			/*tileAttribute ("switch", key: "SECONDARY_CONTROL") {
		    	attributeState "on", icon: "st.switches.light.on", label: 'on', action:"lightOff"
            	attributeState "off", icon: "st.switches.light.off", label: 'off', action: "lightOn"
			}*/
            
            /*tileAttribute ("level", key: "SLIDER_CONTROL") {
    			attributeState "level", action: "switch level.setLevel"
			}*/
            
          
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
		    	attributeState "00", label:'', action:"fanOn", icon:"http://gdurl.com/6vQm" /*backgroundColor:"#FFFFFF",*/ 
                attributeState "05", label:'', action:"fanOn", icon:"http://gdurl.com/6vQm" /*backgroundColor:"#FFFFFF",*/ 
                attributeState "01", label:'', action:"fanOff", icon:"http://gdurl.com/hFV0"/*backgroundColor:"#00A0DC",*/ 
                attributeState "02", label:'', action:"fanOff", icon:"http://gdurl.com/d9p4" /*backgroundColor:"#00A0DC",*/ 
                attributeState "03", label:'', action:"fanOff", icon:"http://gdurl.com/L_sw" /*backgroundColor:"#00A0DC",*/ 
                attributeState "04", label:'', action:"fanOff", icon:"http://gdurl.com/kO65" /*backgroundColor:"#00A0DC",*/ //http://gdurl.com/BhdB
                attributeState "06", label:'', action:"fanOff", icon:"http://gdurl.com/sN0r"/*backgroundColor:"#00A0DC",*/ 
                //attributeState "turningOff", label:'', action:"fanOn", icon:"http://gdurl.com/eanR", /*backgroundColor:"#FFFFFF",*/ nextState:"turningOn"
                //attributeState "turningOn", label:'', action:"fanOff", icon:"http://gdurl.com/eanR", /*backgroundColor:"#00A0DC",*/ nextState:"turningOff"
			}
		}
		
        /*standardTile("switch", "switch", height: 2, width: 2, decoration: "flat"){
        		state "on", icon: "st.switches.light.on", label: '${currentValue}', action:"lightOff", backgroundColor:"#00A0DC", nextState:"turningOff"
        		state "off", icon: "st.switches.light.off", label: '${currentValue}', action:"lightOn", backgroundColor:"#FFFFFF", nextState:"turningOn"
            	state "turningOn", label: "turning on", action:"lightOff", icon:"st.switches.light.on", backgroundColor:"#00A0DC",  nextState:"turningOff"
				state "turningOff", label:"turning off", action:"lightOn", icon:"st.switches.light.off", backgroundColor:"#FFFFFF", nextState:"turningOn"
        }*/
        
        /*controlTile ("level", "device.level", "slider", width: 4, height: 2) {
        	state "level", action: "setLevelBrightness"
    	} */
        
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
   log.debug "description is $description"
   def event = zigbee.getEvent(description) //should map switch and level events
		if (event) {
        log.info "Light event detected on controller: ${event}"
		//if (event.name == "level" && event.value==0) {}
		//else {
			sendEvent(event)
		//}
	} else {
		def map = [:]
		def descMap = zigbee.parseDescriptionAsMap(description)
		if (descMap){
			if (description?.startsWith('read attr -')){
            	log.debug "descMap.clusterInt: ${descMap.clusterInt} descMap.attrInt: ${descMap.attrInt}"
				if (descMap.clusterInt == 514 && descMap.attrInt == 0){
                //log.debug "Parse for fanspeed"
					map.name = "fanSpeed"
					map.value = descMap.value
                    //runIn(1)
                    log.debug "Parse for fanspeed sendEvent for ${map.value}"
					map.descriptionText = "Fan speed is ${map.value}"
                    if (map.value != NULL){
                    	log.debug "map.value: ${map.value}"
                    	if ((map.value != "00")){
                    
                    	
                    	setLastKnownOnSpeed(map.value)
                    }
   
                  	
                    sendEvent(name: "fanSpeed", value: map.value, displayed: true, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
                    updateFanModeSwitches(map.value)
                    }
					log.debug "parse returned ${map}"
					//def fanEvent = createEvent(map)
					//return fanEvent
				}
		   }
		   else if (descMap.clusterInt == 0x0006 && descMap.commandInt == 0x07){
				log.debug "ON/OFF REPORTING CONFIG RESPONSE: " + cluster
				sendEvent(name: "checkInterval", value: 60 * 12, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
			       
			}

		   else {
				log.warn "ON/OFF REPORTING CONFIG FAILED- error code:${cluster.data[0]}"

			}
		  }
		}
	}

def setLastKnownOnSpeed(value){
	log.debug "setLastKnownOnSpeed to $value"
	sendEvent("name": "storedFanSpeed", "value": value)
    
}

def setFanSpeed(value){
	//log.debug "fanSpeed set to $value"
    def intVal= zigbee.convertHexToInt("${value}")
    //log.debug "fanSpeed set to INT ${intVal}"
    zigbee.writeAttribute(0x0202, 0x00, 0x30, intVal)
    //zigbee.writeAttribute(0x0202, 0x00, 0x30, value)

    
}

def updateFanModeSwitches(fan) {

    def fanID = zigbee.convertHexToInt(fan)
	def children = getChildDevices()
   	children.each {child->
    	log.debug "child.currentState(switch): ${child.currentValue("switch")}"
       	def childSpeed = child.getDataValue('speed')
        
        if("${childSpeed}" == "0${fanID}") {	//send ON event to corresponding child fan
        	 
           	child.sendEvent(name:"switch",value:"on")
            //sendEvent(name:"fanSpeed", value: $childSpeed)	//custom icon code
            //sendEvent(name:"switch",value:"on") //send ON event to Fan Parent
        }
        
        
        else if (child.currentValue("switch") == "on"){ 
        		
           		//log.info childSpeedVal
                log.debug "${getFanModeName()[childSpeed]} is turned off"
           		child.sendEvent(name:"switch",value:"off")	//send OFF event to all other child fans
                //child.sendEvent(name:"fanSpeed", value:"off${childSpeedVal}")	//custom icon code
           	
        }
   	}
     //send OFF event to Fan Parent
}
def fanOff() {
	log.debug "fanOff() in parent ran"
	setFanSpeed("00")
    
    
    
}

def fanOn() {
	log.debug "fanOn() in parent ran"
   setFanSpeed(device.currentValue(storedFanSpeed))	
}

def on(){
	zigbee.on()
}

def off(){
	
    zigbee.off()
	
}

def setLevel(value) {
	
	if(value != 0){  zigbee.setLevel(value) + zigbee.on()}
    else{ zigbee.setLevel(value)}
    }
/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
	return zigbee.onOffRefresh()
}

def refresh() {
  //log.debug "${configure()}"
  zigbee.onOffRefresh() + zigbee.levelRefresh() + zigbee.readAttribute(0x0202, 0x0000)
}

def installed() {
	createFanChildren()
	setLastKnownOnSpeed(1) //to make sure value initially saved
    
}

def updated() {
	if(state.oldLabel != device.label){
    	updateChildLabel()
    }
}

def configure() {
   sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
   refresh() + zigbee.onOffConfig(0, 300) + zigbee.levelConfig() + zigbee.configureReporting(0x0202, 0x0000, 0x30, 0, 600, null)
}

def updateChildLabel() {
	log.info "UPDATE LABEL"
	for(i in 0..6) {   		
    	def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    	}                 
        if (childDevice && i != 5) {childDevice.label = "${device.displayName} Mode ${getFanName()["0${i}"]}"} // rename with new label
    }
    
    def childDeviceL = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-Light"
    }
    if (childDeviceL) {childDeviceL.label = "${device.displayName}-Light"}    // rename with new label
}

def createFanChildren() {
	state.oldLabel = device.label
    for (i in 0..6) {
		def childDevice = getChildDevices()?.find {
        	it.device.deviceNetworkId == "${device.deviceNetworkId}-0${i}"
    		}
    
    	if (!childDevice && i != 5){
    			childDevice = addChildDevice("Zigbee Dimmer and Fan Controller - Child Button", "${device.deviceNetworkId}-0${i}", null,[completedSetup: true,
            	label: "${device.displayName} Mode ${getFanModeName()[i]}", isComponent: true, componentName: "fanButton0${i}",
            	componentLabel: "${getFanModeName()[i]}", "data":["speed": "0${i}"]])        	
           		log.info "Creating child fan mode ${childDevice}"
                childDevice.sendEvent(name:"button",value: "${getFanModeName()["0${i}"]}")      ///Can probably get rid of one of these
                childDevice.sendEvent(name:"fanMode",value: "${getFanModeName()["0${i}"]}")
                //childDevice.sendEvent(name:"switch",value: "${getFanModeName()[i]}")            
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
