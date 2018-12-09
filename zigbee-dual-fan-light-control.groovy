/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 *
 *
 
 **/
 
/* Clusters Definitions
        0x0000 Basic
        0x0003 Identify
        0x0004 Groups
        0x0005 Scenes
        0x0006 Light Switch
        0x0008 Light Brightness
        0x0202 Fan Control
        0x0019 OTA Updates
#####################################   
Capability ref for Fan Speed
        name: Fan Speed
		status: proposed
	    attributes:
  		  fanSpeed:
            schema:
              type: object
               properties:
                  value:
                     $ref: PositiveInteger
                  required: ["value"]
                type: NUMBER
                setter: setFanSpeed
       commands:
          setFanSpeed:
            arguments:
            - name: speed
              required: true
              schema:
                $ref: PositiveInteger
              type: NUMBER
        public: true
        id: fanSpeed
        ocfResourceType: x.com.st.fanspeed
        version: 1
 ###################################
 Zigbee ref for Cluster 0x0202 Page 6-33
   Attributes:
            0x0000 |  FanMode         |  enum8   |  0x00 – 0x06   | RW  | 0x05 (auto) | M
            0x0001 |  FanModeSequence |  enum8   |  0x00 – 0x04   | RW  | 0x02        | M

      
    FanMode Attribute Values:
    
            Value   Description
            0x00    Off
            0x01    Low
            0x02    Medium
            0x03    High
            0x04    On
            0x05    Auto (the fan speed is self-regulated)
            0x06    Smart (when the heated/cooled space is occupied, the fan is always on)
            
    FanModeSequence Attribute Values:
    
            Value   Description
            0x00    Low/Med/High
            0x01    Low/High
            0x02    Low/Med/High/Auto
            0x03    Low/High/Auto
            0x04    On/Auto
   Commands: No commands generated or sent
 SO Need to change attribute with write command 
        zigbee.writeAttribute(cluster, attribute set, DataType, payload)
        cluster    0x0202
        attribute  0x00
        DataType   DataType.ENUM8 or 0x30
        Payload    0x00 to 0x06
        
        off:      00  
        low:      01
        med:      02
        med-high: 03
        high:     04
        off:      05
        breeze:   06
        
        zigbee.writeAttribute(0x0202, 0x00, DataType.ENUM8, "${payload}")
	
	
 ########################################
 CONFIGURE
   
   zigbee.configureReporting(Cluster, attributeID, dataType, minReportTime, maxReportTime, reportableChange, additionalParams)
   
   Cluster: The Cluster ID of the requested report
   attributeId: The attribute ID for the requested report
   dataType: The two byte ZigBee type value for the requested report (see DataType)
   minReportTime: Minimum number of seconds between reports
   maxReportTime: Maximum number of seconds between reports
   reportableChange (optional): Amount of change needed to trigger a report. Required for analog data types. Discrete data types should always provide null for this value.
   additionalParams: An optional map to specify additional parameters. See additionalParams for supported attributes.
 
   discrete example for switch
       zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 600, null)
   analog example for level 
       zigbee.configureReporting(0x0008, 0x0000, 0x20, 1, 3600, 0x01)
   would use this for fanSPEED
       zigbee.configureReporting(0x0202, 0x0000, 0x30, 0, 600, null)
       
   for refresh of values
   readAttribute(0x0006, 0x0000) //switch
   readAttribute(0x0008, 0x0000) //level
   readAttribute(0x0202, 0x0000) //fanSPEED
   
   
   
       
   
       

 
 
 
 
   */

 metadata {
  definition (name: "Fan Speed Light Control", namespace: "ccurtiST", author: "Curti", runLocally: false, executeCommandsLocally: false) {
		capability "Actuator"
		capability "Configuration"
		capability "Refresh"
		capability "Switch"
		capability "Switch Level"
		capability "Health Check"
		capability "Light"
    capability "Fan Speed"
    
    command "setFanSpeed", [number]
    command "turnOnBREEZE"
    
    
    attribute "fanSpeed", "number"
    attribute "lastOnSpeed", "number"
    
    
    
    
    fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0202", outClusters: "0003, 0019", model: "HDC52EastwindFan"
  }

tiles(scale: 2) {
		multiAttributeTile(name:"light", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.light.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main "switch"
		details(["switch", "refresh"])
	}
}

def parse(String description) {
   log.debug "description is $description"
   def event = zigbee.getEvent(description) //should map switch and level events
	if (event) {
	   log.debug "lighting event detected fan"
		if (event.name=="level" && event.value==0) {} //avoid redundant reports
		else sendEvent(event)
	} else {
		def map = [:]
		def descMap = zigbee.parseDescriptionAsMap(description)
		if (descMap){
		   if (description?.startsWith('read attr -')){
			if (descMap.clusterInt == 0x0202 && descMap.commandInt == 0x0000){
				map.name = "fanSpeed"
				map.value = descMap.value
				log.debug "parse returned ${map}"
				return createEvent(map)
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
		
			
			
			} else {
				log.warn "ON/OFF REPORTING CONFIG FAILED- error code:${cluster.data[0]}"
			}
		
	}
   
 }

def on(){
   zigbee.on()	
}

def off(){
   zigbee.off()
}

def setLevel(value) {
   zigbee.setLevel(value)
}

def setFanSpeed(speed){
   log.debug "Set Fan Speed to ${speed}"
   zigbee.writeAttribute(0x0202, 0x00, DataType.ENUM8, "${speed}")
}

def turnOnBreeze(){
   log.debug "Breeze Button Pressed"
   setFanSpeed(06)
}

def configure() {
   sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
   refresh() + zigbee.onOffConfig(0, 300) + zigbee.levelConfig() + zigbee.configureReporting(0x0202, 0x0000, 0x30, 0, 600, null)
}

def refresh() {
   zigbee.onOffRefresh() + zigbee.levelRefresh() + zigbee.readAttribute(0x0202, 0x0000)
}

def installed(){
   
}


def updated() {
	
}

def ping() {
   return zigbee.onOffRefresh()
}
