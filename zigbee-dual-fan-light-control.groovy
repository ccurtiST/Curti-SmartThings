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
    
    
    attribute "fanSpeed", "number"
    attribute "lastOnSpeed", "number"
    
    
    
    
    fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 0202", outClusters: "0003, 0019", model: "HDC52EastwindFan"
  }
