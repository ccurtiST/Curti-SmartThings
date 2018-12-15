/**
 *  Zigbee Dimmer and Fan Controller - Child Button
 *
 *  Copyright 2017 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Zigbee Dimmer and Fan Controller - Child Button", namespace: "ccurtist", author: "Christopher Curti") {
	capability "Actuator"
        capability "Button"
	capability "Sensor"
        capability "Switch"
        
        command "click"        
	}
	
	tiles(scale: 2) {
       		standardTile("button", "device.button", width: 1, height: 1, decoration: "flat") {
            		state "LOW", label: "", icon: "http://gdurl.com/Kwcj", backgroundColor: "#FFFFFF", action: "click", nextState: "pushed"
            		state "MED", label: "", icon: "http://gdurl.com/3AJ1", backgroundColor: "#FFFFFF", action: "click", nextState: "pushed"
            		state "MED-HIGH", label: "", icon: "http://gdurl.com/BhdB", backgroundColor: "#FFFFFF", action: "click", nextState: "pushed"
            		state "HIGH", label: "", icon: "http://gdurl.com/ULO7", backgroundColor: "#FFFFFF", action: "click", nextState: "pushed"
            		state "AUTO", label: "", icon: "st.thermostat.fan-auto", backgroundColor: "#FFFFFF", action: "click", nextState: "pushed"
            		state "OFF", label: "", icon: "st.thermostat.fan-off", backgroundColor: "#FFFFFF", action: "click", nextState: "pushed"
            		state "pushed", label: "", icon: "http://gdurl.com/DeZ8", backgroundColor: "#00A0DC"
		} 
	}    
    	main(["button"])        
	details(["button"])   
}

def click(){     
     sendEvent(name: "button", value: "pushed")  
     on()
     refreshValue()
}

def off() {
	def stringForMode = getFanModeName()[getDataValue('speed')]
    log.info "child off(): CHILD ${stringForMode} TURNED OFF"
	sendEvent(name: "switch", value: "${stringForMode}")
}

def on() {
    parent.setFanSpeed(getDataValue("speed"))
}

def refreshValue(){
	log.debug "refresh value on Button ${device.label}"
    sendEvent(name: "button", value: "${getFanModeName()[getDataValue("speed")]}", displayed: false, isStateChange: true) //set value back to the value button was before. Child device label same as button value
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
