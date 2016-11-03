/**
 *  Nest Thermostat
 *	Author: Anthony S. (@tonesto7)
 *	Contributor: Ben W. (@desertBlade) & Eric S. (@E_Sch)
 *  Graphing Modeled on code from Andreas Amann (@ahndee)
 *
 * Based off of the EcoBee thermostat under Templates in the IDE
 * Copyright (C) 2016 Anthony S., Ben W.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.text.SimpleDateFormat
import groovy.time.*

preferences {  }

def devVer() { return "4.0.6"}

// for the UI
metadata {
	definition (name: "${textDevName()}", namespace: "tonesto7", author: "Anthony S.") {
		capability "Actuator"
		capability "Relative Humidity Measurement"
		capability "Refresh"
		capability "Sensor"
		capability "Thermostat"
		capability "Thermostat Cooling Setpoint"
		capability "Thermostat Fan Mode"
		capability "Thermostat Heating Setpoint"
		capability "Thermostat Mode"
		capability "Thermostat Operating State"
		capability "Thermostat Setpoint"
		capability "Temperature Measurement"
		capability "Health Check"

		command "refresh"
		command "poll"

		command "away"
		command "present"
		command "eco"
		//command "setAway"
		//command "setHome"
		command "setPresence"
		//command "setFanMode"
		//command "setTemperature"
		command "setThermostatMode"
		command "levelUpDown"
		command "levelUp"
		command "levelDown"
		command "log"
		command "heatingSetpointUp"
		command "heatingSetpointDown"
		command "coolingSetpointUp"
		command "coolingSetpointDown"
		command "changeMode"
		command "updateNestReportData"

		attribute "temperatureUnit", "string"
		attribute "targetTemp", "string"
		attribute "softwareVer", "string"
		attribute "lastConnection", "string"
		attribute "nestPresence", "string"
		attribute "apiStatus", "string"
		attribute "hasLeaf", "string"
		attribute "debugOn", "string"
		attribute "safetyTempMin", "string"
		attribute "safetyTempMax", "string"
		attribute "safetyTempExceeded", "string"
		attribute "comfortHumidityMax", "string"
		attribute "comfortHumidtyExceeded", "string"
		//attribute "safetyHumidityMin", "string"
		attribute "comfortDewpointMax", "string"
		attribute "comfortDewpointExceeded", "string"
		attribute "tempLockOn", "string"
		attribute "lockedTempMin", "string"
		attribute "lockedTempMax", "string"
		attribute "devTypeVer", "string"
		attribute "onlineStatus", "string"
		attribute "nestPresence", "string"
		attribute "nestThermostatMode", "string"
		attribute "presence", "string"
		attribute "canHeat", "string"
		attribute "canCool", "string"
		attribute "hasFan", "string"
		attribute "sunlightCorrectionEnabled", "string"
		attribute "sunlightCorrectionActive", "string"
		attribute "timeToTarget", "string"
		attribute "nestType", "string"
		attribute "pauseUpdates", "string"
		attribute "nestReportData", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"temperature", type:"thermostat", width:6, height:4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue}°')
			}
			tileAttribute("device.temperature", key: "VALUE_CONTROL") {
				attributeState("default", action: "levelUpDown")
				attributeState("VALUE_UP", action: "levelUp")
				attributeState("VALUE_DOWN", action: "levelDown")
			}
			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'${currentValue}%', unit:"%")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle",			backgroundColor:"#44B621")
				attributeState("heating",		 backgroundColor:"#FFA81E")
				attributeState("cooling",		 backgroundColor:"#2ABBF0")
				attributeState("fan only",		  backgroundColor:"#145D78")
				attributeState("pending heat",	  backgroundColor:"#B27515")
				attributeState("pending cool",	  backgroundColor:"#197090")
				attributeState("vent economizer", backgroundColor:"#8000FF")
			}
			tileAttribute("device.nestThermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label:'${name}')
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}')
				attributeState("auto", label:'${name}')
				attributeState("eco", label:'${name}')
				attributeState("emergency Heat", label:'${name}')
			}
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState("default", label:'${currentValue}')
			}
			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
				attributeState("default", label:'${currentValue}')
			}
		}
		valueTile("temp2", "device.temperature", width: 2, height: 2, decoration: "flat") {
			state("default", label:'${currentValue}°', icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/nest_like.png",
					backgroundColors: getTempColors())
		}
		standardTile("mode2", "device.nestThermostatMode", width: 2, height: 2, decoration: "flat") {
			state("off",  icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/off_icon.png")
			state("heat", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/heat_icon.png")
			state("cool", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/cool_icon.png")
			state("auto", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/heat_cool_icon.png")
			state("eco",  icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/App/eco_icon.png")
		}
		standardTile("thermostatMode", "device.nestThermostatMode", width:2, height:2, decoration: "flat") {
			state("off", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/off_btn_icon.png")
			state("heat", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_btn_icon.png")
			state("cool", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_btn_icon.png")
			state("auto", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_cool_btn_icon.png")
			state("eco", 	action:"changeMode", 	nextState: "updating", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/eco_icon.png")
			state("emergency heat", action:"changeMode", nextState: "updating", icon: "st.thermostat.emergency")
			state("updating", label:"", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cmd_working.png")
		}
	   standardTile("thermostatFanMode", "device.thermostatFanMode", width:2, height:2, decoration: "flat") {
			state "auto",	action:"fanOn", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_auto_icon.png"
			state "on",		action:"fanAuto", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_on_icon.png"
			state "disabled", icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/fan_disabled_icon.png"
		}
		standardTile("nestPresence", "device.nestPresence", width:2, height:2, decoration: "flat") {
			state "home", 		action: "setPresence",	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_home_icon.png"
			state "away", 		action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_away_icon.png"
			state "auto-away", 	action: "setPresence", 	icon: "https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/pres_autoaway_icon.png"
			state "unknown",	action: "setPresence", 	icon: "st.unknown.unknown.unknown"
		}
		standardTile("refresh", "device.refresh", width:2, height:2, decoration: "flat") {
			state "default", label: 'refresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon"
		}
		valueTile("softwareVer", "device.softwareVer", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Firmware:\nv${currentValue}')
		}
		valueTile("hasLeaf", "device.hasLeaf", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Leaf:\n${currentValue}')
		}
		valueTile("onlineStatus", "device.onlineStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state("default", label: 'Network Status:\n${currentValue}')
		}
		valueTile("debugOn", "device.debugOn", width: 2, height: 1, decoration: "flat") {
			state "true", 	label: 'Debug:\n${currentValue}'
			state "false", 	label: 'Debug:\n${currentValue}'
		}
		valueTile("devTypeVer", "device.devTypeVer",  width: 2, height: 1, decoration: "flat") {
			state("default", label: 'Device Type:\nv${currentValue}')
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", width: 1, height: 1) {
			state("heatingSetpoint", label:'${currentValue}', unit: "Heat", foregroundColor: "#FFFFFF",
				backgroundColors: [ [value: 0, color: "#FFFFFF"], [value: 7, color: "#FF3300"], [value: 15, color: "#FF3300"] ])
			state("disabled" , label: '', foregroundColor: "#FFFFFF", backgroundColor: "#FFFFFF")
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", width: 1, height: 1) {
			state("coolingSetpoint", label: '${currentValue}', unit: "Cool", foregroundColor: "#FFFFFF",
				backgroundColors: [ [value: 0, color: "#FFFFFF"], [value: 7, color: "#0099FF"], [value: 15, color: "#0099FF"] ])
			state("disabled", label: '', foregroundColor: "#FFFFFF", backgroundColor: "#FFFFFF")
		}
		standardTile("heatingSetpointUp", "device.heatingSetpoint", width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
			state "default", label: '', action:"heatingSetpointUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_up.png"
			state "", label: ''
		}
		standardTile("heatingSetpointDown", "device.heatingSetpoint",  width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
			state "default", label:'', action:"heatingSetpointDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/heat_arrow_down.png"
			state "", label: ''
		}

		controlTile("heatSliderControl", "device.heatingSetpoint", "slider", height: 2, width: 3, inactiveLabel: false) {
			state "default", action:"setHeatingSetpoint", backgroundColor:"#FF3300"
			state "", label: ''
		}

		standardTile("coolingSetpointUp", "device.coolingSetpoint", width: 1, height: 1,canChangeIcon: false, decoration: "flat") {
			state "default", label:'', action:"coolingSetpointUp", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_up.png"
			state "", label: ''
		}
		standardTile("coolingSetpointDown", "device.coolingSetpoint", width: 1, height: 1, canChangeIcon: false, decoration: "flat") {
			state "default", label:'', action:"coolingSetpointDown", icon:"https://raw.githubusercontent.com/tonesto7/nest-manager/master/Images/Devices/cool_arrow_down.png"
			state "", label: ''
		}

		controlTile("coolSliderControl", "device.coolingSetpoint", "slider", height: 2, width: 3, inactiveLabel: false) {
			state "setCoolingSetpoint", action:"setCoolingSetpoint", backgroundColor:"#0099FF"
			state "", label: ''
		}

		valueTile("lastConnection", "device.lastConnection", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Nest Checked-In At:\n${currentValue}')
		}
		valueTile("lastUpdatedDt", "device.lastUpdatedDt", width: 4, height: 1, decoration: "flat", wordWrap: true) {
			state("default", label: 'Data Last Received:\n${currentValue}')
		}
		valueTile("apiStatus", "device.apiStatus", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state "ok", label: "API Status:\nOK"
			state "issue", label: "API Status:\nISSUE ", backgroundColor: "#FFFF33"
		}
		valueTile("weatherCond", "device.weatherCond", width: 2, height: 1, wordWrap: true, decoration: "flat") {
			state "default", label:'${currentValue}'
		}

		htmlTile(name:"graphHTML", action: "getGraphHTML", width: 6, height: 8, whitelist: ["www.gstatic.com", "raw.githubusercontent.com", "cdn.rawgit.com"])

		main("temp2")
		details( ["temperature", "thermostatMode", "nestPresence", "thermostatFanMode", "heatingSetpointDown", "heatingSetpoint", "heatingSetpointUp",
				  "coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "graphHTML", "refresh"])
				  //"coolingSetpointDown", "coolingSetpoint", "coolingSetpointUp", "graphHTML", "heatSliderControl", "coolSliderControl", "refresh"] )
	}
}

def getTempColors() {
	def colorMap
	if (wantMetric()) {
		colorMap = [
			// Celsius Color Range
			[value: 0, color: "#153591"],
			[value: 7, color: "#1e9cbb"],
			[value: 15, color: "#90d2a7"],
			[value: 23, color: "#44b621"],
			[value: 29, color: "#f1d801"],
			[value: 33, color: "#d04e00"],
			[value: 36, color: "#bc2323"]
			]
	} else {
		colorMap = [
			// Fahrenheit Color Range
			[value: 40, color: "#153591"],
			[value: 44, color: "#1e9cbb"],
			[value: 59, color: "#90d2a7"],
			[value: 74, color: "#44b621"],
			[value: 84, color: "#f1d801"],
			[value: 92, color: "#d04e00"],
			[value: 96, color: "#bc2323"]
		]
	}
}

mappings {
	path("/getGraphHTML") {action: [GET: "getGraphHTML"]}
}

def initialize() {
	Logger("initialize")
}

def installed() {
	Logger("installed...")
	// Notify health check about this device with timeout interval 30 minutes
	//sendEvent(name: "checkInterval", value: 30 * 60, data: [protocol: "lan", hubHardwareId: device.hub.hardwareID], displayed: false)
}

def ping() {
	Logger("ping...")
	refresh()
}

def parse(String description) {
	LogAction("Parsing '${description}'")
}

def poll() {
	Logger("Polling parent...")
	poll()
}

def refresh() {
	pauseEvent("false")
	parent.refresh(this)
}

// parent calls this method to queue data.
// goal is to return to parent asap to avoid execution timeouts

def generateEvent(Map eventData) {
	//LogAction("generateEvent Parsing data ${eventData}", "trace")
	def eventDR = [evt:eventData]
	runIn(8, "processEvent", [overwrite: true, data: eventDR] )
}

def processEvent(data) {
	if(state?.swVersion != devVer()) {
		installed()
		state.swVersion = devVer()
	}
	def pauseUpd = !device.currentValue("pauseUpdates") ? false : device.currentValue("pauseUpdates").value
	if(pauseUpd == "true") { LogAction("pausing", "warn"); return }

	def eventData = data?.evt
	state.remove("eventData")

	//LogAction("processEvent Parsing data ${eventData}", "trace")
	try {
		LogAction("------------START OF API RESULTS DATA------------", "warn")
		if(eventData) {
			if(virtType()) { nestTypeEvent("virtual") } else { nestTypeEvent("physical") }
			state.clientBl = eventData?.clientBl == true ? true : false
			state.showLogNamePrefix = eventData?.logPrefix == true ? true : false
			state.curExtTemp = eventData?.curExtTemp
			state.useMilitaryTime = eventData?.mt ? true : false
			state.nestTimeZone = !location?.timeZone ? eventData.tz : null
			debugOnEvent(eventData?.debug ? true : false)
			tempUnitEvent(getTemperatureScale())
			if(eventData?.data?.is_locked != null) { tempLockOnEvent(eventData?.data?.is_locked.toString() == "true" ? true : false) }
			canHeatCool(eventData?.data?.can_heat, eventData?.data?.can_cool)
			hasFan(eventData?.data?.has_fan.toString())
			presenceEvent(eventData?.pres.toString())
			hvacModeEvent(eventData?.data?.hvac_mode.toString())
			hasLeafEvent(eventData?.data?.has_leaf)
			humidityEvent(eventData?.data?.humidity.toString())
			operatingStateEvent(eventData?.data?.hvac_state.toString())
			fanModeEvent(eventData?.data?.fan_timer_active.toString())
			if(!eventData?.data?.last_connection) { lastCheckinEvent(null) }
			else { lastCheckinEvent(eventData?.data?.last_connection) }

			sunlightCorrectionEnabledEvent(eventData?.data?.sunlight_correction_enabled)
			sunlightCorrectionActiveEvent(eventData?.data?.sunlight_correction_active)
			timeToTargetEvent(eventData?.data?.time_to_target, eventData?.data?.time_to_target_training)
			softwareVerEvent(eventData?.data?.software_version.toString())
			onlineStatusEvent(eventData?.data?.is_online.toString())
			deviceVerEvent(eventData?.latestVer.toString())
			apiStatusEvent(eventData?.apiIssues)
			state?.childWaitVal = eventData?.childWaitVal.toInteger()
			if(eventData?.htmlInfo) { state?.htmlInfo = eventData?.htmlInfo }
			if(eventData?.allowDbException) { state?.allowDbException = eventData?.allowDbException = false ? false : true }
			if(eventData?.safetyTemps) { safetyTempsEvent(eventData?.safetyTemps) }
			if(eventData?.comfortHumidity) { comfortHumidityEvent(eventData?.comfortHumidity) }
			if(eventData?.comfortDewpoint) { comfortDewpointEvent(eventData?.comfortDewpoint) }
			state.voiceReportPrefs = eventData?.vReportPrefs
			def hvacMode = state?.hvac_mode
			def tempUnit = state?.tempUnit
			switch (tempUnit) {
				case "C":
					def heatingSetpoint = 0.0
					def coolingSetpoint = 0.0
					def temp = eventData?.data?.ambient_temperature_c.toDouble()
					def targetTemp = eventData?.data?.target_temperature_c.toDouble()

					if (hvacMode == "cool") {
						coolingSetpoint = targetTemp
						//clearHeatingSetpoint()
					}
					else if (hvacMode == "heat") {
						heatingSetpoint = targetTemp
						//clearCoolingSetpoint()
					}
					else if (hvacMode == "auto") {
						coolingSetpoint = Math.round(eventData?.data?.target_temperature_high_c.toDouble())
						heatingSetpoint = Math.round(eventData?.data?.target_temperature_low_c.toDouble())
					}
					if(!state?.present || state?.nestHvac_mode == "eco") {
						if(eventData?.data?.eco_temperature_high_c) { coolingSetpoint = eventData?.data?.eco_temperature_high_c.toDouble() }
						else if(eventData?.data?.away_temperature_high_c) { coolingSetpoint = eventData?.data?.away_temperature_high_c.toDouble() }
						if(eventData?.data?.eco_temperature_low_c) { heatingSetpoint = eventData?.data?.eco_temperature_low_c.toDouble() }
						else if(eventData?.data?.away_temperature_low_c) { heatingSetpoint = eventData?.data?.away_temperature_low_c.toDouble() }
					}
					temperatureEvent(temp)
					thermostatSetpointEvent(targetTemp)
					coolingSetpointEvent(coolingSetpoint)
					heatingSetpointEvent(heatingSetpoint)
					if(eventData?.data?.locked_temp_min_c && eventData?.data?.locked_temp_max_c) { lockedTempEvent(eventData?.data?.locked_temp_min_c, eventData?.data?.locked_temp_max_c) }
					break

				case "F":
					def heatingSetpoint = 0
					def coolingSetpoint = 0
					def temp = eventData?.data?.ambient_temperature_f
					def targetTemp = eventData?.data?.target_temperature_f

					if (hvacMode == "cool") {
						coolingSetpoint = targetTemp
						//clearHeatingSetpoint()
					}
					else if (hvacMode == "heat") {
						heatingSetpoint = targetTemp
						//clearCoolingSetpoint()
					}
					else if (hvacMode == "auto") {
						coolingSetpoint = eventData?.data?.target_temperature_high_f
						heatingSetpoint = eventData?.data?.target_temperature_low_f
					}
					if (!state?.present || state?.nestHvac_mode == "eco") {
						if(eventData?.data?.eco_temperature_high_f) { coolingSetpoint = eventData?.data?.eco_temperature_high_f }
						else if(eventData?.data?.away_temperature_high_f) { coolingSetpoint = eventData?.data?.away_temperature_high_f }
						if(eventData?.data?.eco_temperature_low_f)  { heatingSetpoint = eventData?.data?.eco_temperature_low_f }
						else if(eventData?.data?.away_temperature_low_f)  { heatingSetpoint = eventData?.data?.away_temperature_low_f }
					}
					temperatureEvent(temp)
					thermostatSetpointEvent(targetTemp)
					coolingSetpointEvent(coolingSetpoint)
					heatingSetpointEvent(heatingSetpoint)
					if(eventData?.data?.locked_temp_min_f && eventData?.data?.locked_temp_max_f) { lockedTempEvent(eventData?.data?.locked_temp_min_f, eventData?.data?.locked_temp_max_f) }
					break

				default:
					Logger("no Temperature data $tempUnit")
					break
			}
			getSomeData(true)
			lastUpdatedEvent()
		}
		//This will return all of the devices state data to the logs.
		//LogAction("Device State Data: ${getState()}")
		return null
	}
	catch (ex) {
		log.error "generateEvent Exception:", ex
		exceptionDataHandler(ex.message, "generateEvent")
	}
}

def getStateSize()	{ return state?.toString().length() }
def getStateSizePerc()  { return (int) ((stateSize/100000)*100).toDouble().round(0) }

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def getDeviceStateData() {
	return getState()
}

def getTimeZone() {
	def tz = null
	if (location?.timeZone) { tz = location?.timeZone }
	else { tz = state?.nestTimeZone ? TimeZone.getTimeZone(state?.nestTimeZone) : null }
	if(!tz) { Logger("getTimeZone: Hub or Nest TimeZone is not found ...", "warn") }
	return tz
}

def tUnitStr() {
	return "°${state?.tempUnit}"
}

def isCodeUpdateAvailable(newVer, curVer) {
	def result = false
	def latestVer
	def versions = [newVer, curVer]
	if(newVer != curVer) {
		latestVer = versions?.max { a, b ->
			def verA = a?.tokenize('.')
			def verB = b?.tokenize('.')
			def commonIndices = Math.min(verA?.size(), verB?.size())
			for (int i = 0; i < commonIndices; ++i) {
				if (verA[i]?.toInteger() != verB[i]?.toInteger()) {
					return verA[i]?.toInteger() <=> verB[i]?.toInteger()
				}
			}
			verA?.size() <=> verB?.size()
		}
		result = (latestVer == newVer) ? true : false
	}
	LogAction("isCodeUpdateAvailable(): newVer: $newVer | curVer: $curVer | newestVersion: ${latestVer} | result: $result")
	return result
}

def pauseEvent(val) {
	def curData = device.currentState("pauseUpdates")?.value
	if(!curData?.equals(val)) {
		Logger("UPDATED | Pause Updates is: (${val}) | Original State: (${curData})")
		sendEvent(name: 'pauseUpdates', value: val, displayed: false)
	} else { LogAction("Pause Updates is: (${val}) | Original State: (${curData})") }
}

def deviceVerEvent(ver) {
	def curData = device.currentState("devTypeVer")?.value.toString()
	def pubVer = ver ?: null
	def dVer = devVer() ?: null
	state.updateAvailable = isCodeUpdateAvailable(pubVer, dVer)
	def newData = state.updateAvailable ? "${dVer}(New: v${pubVer})" : "${dVer}" as String
	state.devTypeVer = newData
		//log.info "curData: ${curData.getProperties().toString()},  newData: ${newData.getProperties().toString()}"
	if(!curData?.equals(newData)) {
		Logger("UPDATED | Device Type Version is: (${newData}) | Original State: (${curData})")
		sendEvent(name: 'devTypeVer', value: newData, displayed: false)
	} else { LogAction("Device Type Version is: (${newData}) | Original State: (${curData})") }
}

def nestTypeEvent(type) {
	def val = device.currentState("nestType")?.value
	state?.nestType=type
	if(!val.equals(type)) {
		Logger("UPDATED | nestType: (${type}) | Original State: (${val})")
		sendEvent(name: 'nestType', value: type, displayed: true)
	} else { LogAction("nestType: (${type}) | Original State: (${val})") }
}

def sunlightCorrectionEnabledEvent(sunEn) {
	def val = device.currentState("sunlightCorrectionEnabled")?.value
	def newVal = sunEn.toString() == "true" ? true : false
	if(!val.equals(newVal.toString())) {
		Logger("UPDATED | SunLight Correction Enabled: (${newVal}) | Original State: (${val.toString().capitalize()})")
		sendEvent(name: 'sunlightCorrectionEnabled', value: newVal, displayed: false)
	} else { LogAction("SunLight Correction Enabled: (${newVal}) | Original State: (${val})") }
}

def sunlightCorrectionActiveEvent(sunAct) {
	def val = device.currentState("sunlightCorrectionActive")?.value
	def newVal = sunAct.toString() == "true" ? true : false
	if(!val.equals(newVal.toString())) {
		Logger("UPDATED | SunLight Correction Active: (${newVal}) | Original State: (${val.toString().capitalize()})")
		sendEvent(name: 'sunlightCorrectionActive', value: newVal, displayed: false)
	} else { LogAction("SunLight Correction Active: (${newVal}) | Original State: (${val})") }
}

def timeToTargetEvent(ttt, tttTr) {
	def val = device.currentState("timeToTarget")?.stringValue
	def opIdle = device.currentState("thermostatOperatingState").stringValue == "idle" ? true : false
	//log.debug "opIdle: $opIdle"
	def nVal
	if(ttt) {
		nVal = ttt.toString().replaceAll("\\~", "").toInteger()
	}
	//log.debug "nVal: $nVal"
	def trStr
	if(tttTr) {
		trStr = tttTr.toString() == "training" ? "\n(Still Training)" : ""
	}
	def newVal = ttt ? (nVal == 0 && opIdle ? "System is Idle" : "${nVal} Minutes${trStr}") : "Not Available"
	if(!val.equals(newVal.toString())) {
		Logger("UPDATED | Time to Target: (${newVal}) | Original State: (${val.toString().capitalize()})")
		sendEvent(name: 'timeToTarget', value: newVal, displayed: false)
	} else { LogAction("Time to Target: (${newVal}) | Original State: (${val})") }
}

def debugOnEvent(debug) {
	def val = device.currentState("debugOn")?.value
	def dVal = debug ? "On" : "Off"
	state?.debugStatus = dVal
	state?.debug = debug.toBoolean() ? true : false
	if(!val.equals(dVal)) {
		Logger("UPDATED | debugOn: (${dVal}) | Original State: (${val.toString().capitalize()})")
		sendEvent(name: 'debugOn', value: dVal, displayed: false)
	} else { LogAction("debugOn: (${dVal}) | Original State: (${val})") }
}

def lastCheckinEvent(checkin) {
	//LogAction("lastCheckinEvent()...", "trace")
	def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
	tf.setTimeZone(getTimeZone())
	def lastConn = checkin ? "${tf?.format(Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", checkin))}" : "Not Available"
	def lastChk = device.currentState("lastConnection")?.value
	state?.lastConnection = lastConn?.toString()
	if(!lastChk.equals(lastConn?.toString())) {
		Logger("UPDATED | Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})")
		sendEvent(name: 'lastConnection', value: lastConn?.toString(), displayed: false, isStateChange: true)
	} else { LogAction("Last Nest Check-in was: (${lastConn}) | Original State: (${lastChk})") }
}

def lastUpdatedEvent() {
	def now = new Date()
	def formatVal = state.useMilitaryTime ? "MMM d, yyyy - HH:mm:ss" : "MMM d, yyyy - h:mm:ss a"
	def tf = new SimpleDateFormat(formatVal)
	tf.setTimeZone(getTimeZone())
	def lastDt = "${tf?.format(now)}"
	def lastUpd = device.currentState("lastUpdatedDt")?.value
	state?.lastUpdatedDt = lastDt?.toString()
	if(!lastUpd.equals(lastDt?.toString())) {
		LogAction("Last Parent Refresh time: (${lastDt}) | Previous Time: (${lastUpd})")
		sendEvent(name: 'lastUpdatedDt', value: lastDt?.toString(), displayed: false, isStateChange: true)
	}
}

def softwareVerEvent(ver) {
	def verVal = device.currentState("softwareVer")?.value
	state?.softwareVer = ver
	if(!verVal.equals(ver)) {
		Logger("UPDATED | Firmware Version: (${ver}) | Original State: (${verVal})")
		sendEvent(name: 'softwareVer', value: ver, descriptionText: "Firmware Version is now ${ver}", displayed: false, isStateChange: true)
	} else { LogAction("Firmware Version: (${ver}) | Original State: (${verVal})") }
}

def tempUnitEvent(unit) {
	def tmpUnit = device.currentState("temperatureUnit")?.value
	state?.tempUnit = unit
	if(!tmpUnit.equals(unit)) {
		Logger("UPDATED | Temperature Unit: (${unit}) | Original State: (${tmpUnit})")
		sendEvent(name:'temperatureUnit', value: unit, descriptionText: "Temperature Unit is now: '${unit}'", displayed: true, isStateChange: true)
	} else { LogAction("Temperature Unit: (${unit}) | Original State: (${tmpUnit})") }
}

// TODO NOT USED
def targetTempEvent(Double targetTemp) {
	def temp = device.currentState("targetTemperature")?.value.toString()
	def rTargetTemp = wantMetric() ? targetTemp.round(1) : targetTemp.round(0).toInteger()
	if(!temp.equals(rTargetTemp.toString())) {
		Logger("UPDATED | targetTemperature is (${rTargetTemp}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})")
		sendEvent(name:'targetTemperature', value: rTargetTemp, unit: state?.tempUnit, descriptionText: "Target Temperature is ${rTargetTemp}${tUnitStr()}", displayed: false, isStateChange: true)
	} else { LogAction("targetTemperature is (${rTargetTemp}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})") }
}

def thermostatSetpointEvent(Double targetTemp) {
	def temp = device.currentState("thermostatSetpoint")?.value.toString()
	def rTargetTemp = wantMetric() ? targetTemp.round(1) : targetTemp.round(0).toInteger()
	if(!temp.equals(rTargetTemp.toString())) {
		Logger("UPDATED | thermostatSetPoint Temperature is (${rTargetTemp}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})")
		sendEvent(name:'thermostatSetpoint', value: rTargetTemp, unit: state?.tempUnit, descriptionText: "thermostatSetpoint Temperature is ${rTargetTemp}${tUnitStr()}", displayed: false, isStateChange: true)
	} else { LogAction("thermostatSetpoint is (${rTargetTemp}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})") }
}

def temperatureEvent(Double tempVal) {
	try {
		def temp = device.currentState("temperature")?.value.toString()
		def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
		if(!temp.equals(rTempVal.toString())) {
			Logger("UPDATED | Temperature is (${rTempVal}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})")
			sendEvent(name:'temperature', value: rTempVal, unit: state?.tempUnit, descriptionText: "Ambient Temperature is ${rTempVal}${tUnitStr()}" , displayed: true, isStateChange: true)
		} else { LogAction("Temperature is (${rTempVal}${tUnitStr()}) | Original Temp: (${temp})${tUnitStr()}") }
		checkSafetyTemps()
	}
	catch (ex) {
		log.error "temperatureEvent Exception:", ex
		exceptionDataHandler(ex.message, "temperatureEvent")
	}
}

def heatingSetpointEvent(Double tempVal) {
	def temp = device.currentState("heatingSetpoint")?.value.toString()
	if(tempVal.toInteger() == 0 || !state?.can_heat || (getHvacMode == "off")) {
		if(temp != "") { clearHeatingSetpoint() }
	} else {
		def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
		if(!temp.equals(rTempVal.toString())) {
			Logger("UPDATED | Heat Setpoint is (${rTempVal}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})")
			def disp = false
			def hvacMode = getHvacMode()
			if (hvacMode in ["auto", "heat"]) { disp = true }
			sendEvent(name:'heatingSetpoint', value: rTempVal, unit: state?.tempUnit, descriptionText: "Heat Setpoint is ${rTempVal}${tUnitStr()}" , displayed: disp, isStateChange: true, state: "heat")
		} else { LogAction("Heat Setpoint is (${rTempVal}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})") }
	}
}

def coolingSetpointEvent(Double tempVal) {
	def temp = device.currentState("coolingSetpoint")?.value.toString()
	if(tempVal.toInteger() == 0 || !state?.can_cool || (getHvacMode == "off")) {
		if(temp != "") { clearCoolingSetpoint() }
	} else {
		def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
		if(!temp.equals(rTempVal.toString())) {
			Logger("UPDATED | Cool Setpoint is (${rTempVal}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})")
			def disp = false
			def hvacMode = getHvacMode()
			if (hvacMode in ["auto", "cool"]) { disp = true }
			sendEvent(name:'coolingSetpoint', value: rTempVal, unit: state?.tempUnit, descriptionText: "Cool Setpoint is ${rTempVal}${tUnitStr()}" , displayed: disp, isStateChange: true, state: "cool")
		} else { LogAction("Cool Setpoint is (${rTempVal}${tUnitStr()}) | Original Temp: (${temp}${tUnitStr()})") }
	}
}

def hasLeafEvent(Boolean hasLeaf) {
	def leaf = device.currentState("hasLeaf")?.value
	def lf = hasLeaf ? "On" : "Off"
	state?.hasLeaf = hasLeaf
	if(!leaf.equals(lf)) {
		Logger("UPDATED | Leaf is set to (${lf}) | Original State: (${leaf})")
		sendEvent(name:'hasLeaf', value: lf,  descriptionText: "Leaf: ${lf}" , displayed: false, isStateChange: true, state: lf)
	} else { LogAction("Leaf is set to (${lf}) | Original State: (${leaf})") }
}

def humidityEvent(humidity) {
	def hum = device.currentState("humidity")?.value
	if(!hum.equals(humidity)) {
		Logger("UPDATED | Humidity is (${humidity}) | Original State: (${hum})")
		sendEvent(name:'humidity', value: humidity, unit: "%", descriptionText: "Humidity is ${humidity}" , displayed: false, isStateChange: true)
	} else { LogAction("Humidity is (${humidity}) | Original State: (${hum})") }
}

def presenceEvent(presence) {
	def val = getPresence()
	def pres = (presence == "home") ? "present" : "not present"
	def nestPres = state?.nestPresence
	def newNestPres = (presence == "home") ? "home" : ((presence == "auto-away") ? "auto-away" : "away")
	def statePres = state?.present
	state?.present = (pres == "present") ? true : false
	state?.nestPresence = newNestPres
	if(!val.equals(pres) || !nestPres.equals(newNestPres) || !nestPres) {
		Logger("UPDATED | Presence: ${pres.toString().capitalize()} | Original State: ${val.toString().capitalize()} | State Variable: ${statePres}")
		sendEvent(name: 'presence', value: pres, descriptionText: "Device is: ${pres}", displayed: false, isStateChange: true, state: pres )
		sendEvent(name: 'nestPresence', value: newNestPres, descriptionText: "Nest Presence is: ${newNestPres}", displayed: true, isStateChange: true )
	} else { LogAction("Presence - Present: (${pres}) | Original State: (${val}) | State Variable: ${state?.present}") }
}

def hvacModeEvent(mode) {
	def hvacMode = !state?.hvac_mode ? device.currentState("thermostatMode")?.value.toString() : state.hvac_mode
	def newMode = (mode == "heat-cool") ? "auto" : mode
	if(mode == "eco") {
		if(state?.can_cool && state?.can_heat) { newMode = "auto" }
		else if(state?.can_heat) { newMode = "heat" }
		else if(state?.can_cool) { newMode = "cool" }
	}
	state?.hvac_mode = newMode
	if(!hvacMode.equals(newMode)) {
		Logger("UPDATED | Hvac Mode is (${newMode.toString().capitalize()}) | Original State: (${hvacMode.toString().capitalize()})")
		sendEvent(name: "thermostatMode", value: newMode, descriptionText: "HVAC mode is ${newMode} mode", displayed: true, isStateChange: true)
	} else { LogAction("Hvac Mode is (${newMode}) | Original State: (${hvacMode})") }

	def oldnestmode = state?.nestHvac_mode
	newMode = (mode == "heat-cool") ? "auto" : mode
	state?.nestHvac_mode = newMode
	if(!oldnestmode.equals(newMode)) {
		Logger("UPDATED | NEST Hvac Mode is (${newMode.toString().capitalize()}) | Original State: (${oldnestmode.toString().capitalize()})")
		sendEvent(name: "nestThermostatMode", value: newMode, descriptionText: "Nest HVAC mode is ${newMode} mode", displayed: true, isStateChange: true)
	} else { LogAction("NEST Hvac Mode is (${newMode}) | Original State: (${oldnestmode})") }
}

def fanModeEvent(fanActive) {
	def val = state?.has_fan ? ((fanActive == "true") ? "on" : "auto") : "disabled"
	def fanMode = device.currentState("thermostatFanMode")?.value
	if(!fanMode.equals(val)) {
		Logger("UPDATED | Fan Mode: (${val.toString().capitalize()}) | Original State: (${fanMode.toString().capitalize()})")
		sendEvent(name: "thermostatFanMode", value: val, descriptionText: "Fan Mode is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Fan Active: (${val}) | Original State: (${fanMode})") }

}

def operatingStateEvent(operatingState) {
	def hvacState = device.currentState("thermostatOperatingState")?.value
	def operState = (operatingState == "off") ? "idle" : operatingState
	if(!hvacState.equals(operState)) {
		Logger("UPDATED | OperatingState is (${operState.toString().capitalize()}) | Original State: (${hvacState.toString().capitalize()})")
		sendEvent(name: 'thermostatOperatingState', value: operState, descriptionText: "Device is ${operState}", displayed: true, isStateChange: true)
	} else { LogAction("OperatingState is (${operState}) | Original State: (${hvacState})") }

}

def tempLockOnEvent(isLocked) {
	def curState = device.currentState("tempLockOn")?.value.toString()
	def newState = isLocked?.toString()
	state?.tempLockOn = newState
	if(!curState?.equals(newState)) {
		Logger("UPDATED | Temperature Lock is set to (${newState}) | Original State: (${curState})")
		sendEvent(name:'tempLockOn', value: newState,  descriptionText: "Temperature Lock: ${newState}" , displayed: false, isStateChange: true, state: newState)
	} else { LogAction("Temperature Lock is set to (${newState}) | Original State: (${curState})") }
}

def lockedTempEvent(Double minTemp, Double maxTemp) {
	def curMinTemp = device.currentState("lockedTempMin")?.doubleValue
	def curMaxTemp = device.currentState("lockedTempMax")?.doubleValue
	//def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
	if(curMinTemp != minTemp || curMaxTemp != maxTemp) {
		Logger("UPDATED | Temperature Lock Minimum is (${minTemp}) | Original Temp: (${curMinTemp})")
		Logger("UPDATED | Temperature Lock Maximum is (${maxTemp}) | Original Temp: (${curMaxTemp})")
		sendEvent(name:'lockedTempMin', value: minTemp, unit: state?.tempUnit, descriptionText: "Temperature Lock Minimum is ${minTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
		sendEvent(name:'lockedTempMax', value: maxTemp, unit: state?.tempUnit, descriptionText: "Temperature Lock Maximum is ${maxTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
	} else {
		LogAction("Temperature Lock Minimum is (${minTemp}${state?.tempUnit}) | Original Minimum Temp: (${curMinTemp}${state?.tempUnit})")
		LogAction("Temperature Lock Maximum is (${maxTemp}${state?.tempUnit}) | Original Maximum Temp: (${curMaxTemp}${state?.tempUnit})")
	}
}

def safetyTempsEvent(safetyTemps) {
	def curMinTemp = device.currentState("safetyTempMin")?.doubleValue
	def curMaxTemp = device.currentState("safetyTempMax")?.doubleValue
	def newMinTemp = safetyTemps?.min.toDouble() ?: 0
	def newMaxTemp = safetyTemps?.max.toDouble() ?: 0

	//def rTempVal = wantMetric() ? tempVal.round(1) : tempVal.round(0).toInteger()
	if(curMinTemp != newMinTemp || curMaxTemp != newMaxTemp) {
		Logger("UPDATED | Safety Temperature Minimum is (${newMinTemp}${state?.tempUnit}) | Original Temp: (${curMinTemp}${state?.tempUnit})")
		Logger("UPDATED | Safety Temperature Maximum is (${newMaxTemp}${state?.tempUnit}) | Original Temp: (${curMaxTemp}${state?.tempUnit})")
		sendEvent(name:'safetyTempMin', value: newMinTemp, unit: state?.tempUnit, descriptionText: "Safety Temperature Minimum is ${newMinTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
		sendEvent(name:'safetyTempMax', value: newMaxTemp, unit: state?.tempUnit, descriptionText: "Safety Temperature Maximum is ${newMaxTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
		checkSafetyTemps()
	} else {
		LogAction("Safety Temperature Minimum is  (${newMinTemp}${state?.tempUnit}) | Original Minimum Temp: (${curMinTemp}${state?.tempUnit})")
		LogAction("Safety Temperature Maximum is  (${newMaxTemp}${state?.tempUnit}) | Original Maximum Temp: (${curMaxTemp}${state?.tempUnit})")
	}
}

def checkSafetyTemps() {
	def curMinTemp = device.currentState("safetyTempMin")?.doubleValue
	def curMaxTemp = device.currentState("safetyTempMax")?.doubleValue
	def curTemp = device.currentState("temperature")?.doubleValue
	def curRangeStr = device.currentState("safetyTempExceeded")?.toString()
	def curInRange = !curRangeStr?.toBoolean()
	def inRange = true
	if(curMinTemp && curMaxTemp) {
		if((curMinTemp > curTemp || curMaxTemp < curTemp)) { inRange = false }
	}
	//LogAction("curMin: ${curMinTemp}  curMax: ${curMaxTemp} curTemp: ${curTemp} curinRange: ${curinRange} inRange: ${inRange}")
	if (curRangeStr == null || inRange != curInRange) {
		sendEvent(name:'safetyTempExceeded', value: (inRange ? "false" : "true"),  descriptionText: "Safety Temperature ${inRange ? "OK" : "Exceeded"} ${curTemp}${state?.tempUnit}" , displayed: true, isStateChange: true)
		Logger("UPDATED | Safety Temperature Exceeded is (${inRange ? "false" : "true"}) | Current Temp: (${curTemp}${state?.tempUnit})")
	} else {
		LogAction("Safety Temperature Exceeded is (${inRange ? "false" : "true"}) | Current Temp: (${curTemp}${state?.tempUnit})")
	}
}

def comfortHumidityEvent(comfortHum) {
	//def curMinHum = device.currentState("comfortHumidityMin")?.integerValue
	def curMaxHum = device.currentState("comfortHumidityMax")?.integerValue
	//def newMinHum = comfortHum?.min.toInteger() ?: 0
	def newMaxHum = comfortHum?.toInteger() ?: 0
	if(curMaxHum != newMaxHum) {
		//LogAction("UPDATED | Comfort Humidity Minimum is (${newMinHum}) | Original Temp: (${curMinHum})")
		Logger("UPDATED | Comfort Humidity Maximum is (${newMaxHum}%) | Original Humidity: (${curMaxHum}%)")
		sendEvent(name:'comfortHumidityMax', value: newMaxHum, unit: "%", descriptionText: "Safety Humidity Maximum is ${newMaxHum}%" , displayed: true, isStateChange: true)
	} else {
		//LogAction("Comfort Humidity Minimum is (${newMinHum}) | Original Minimum Humidity: (${curMinHum})")
		LogAction("Comfort Humidity Maximum is (${newMaxHum}%) | Original Maximum Humidity: (${curMaxHum}%)")
	}
}

def comfortDewpointEvent(comfortDew) {
	//def curMinDew = device.currentState("comfortDewpointMin")?.integerValue
	def curMaxDew = device.currentState("comfortDewpointMax")?.doubleValue
	//def newMinDew = comfortDew?.min.toInteger() ?: 0
	def newMaxDew = comfortDew?.toDouble() ?: 0.0
	if(curMaxDew != newMaxDew) {
		//LogAction("UPDATED | Comfort Dewpoint Minimum is (${newMinDew}) | Original Temp: (${curMinDew})")
		Logger("UPDATED | Comfort Dewpoint Maximum is (${newMaxDew}) | Original Dewpoint: (${curMaxDew})")
		//sendEvent(name:'comfortDewpointMin', value: newMinDew, unit: "%", descriptionText: "Comfort Dewpoint Minimum is ${newMinDew}" , displayed: true, isStateChange: true)
		sendEvent(name:'comfortDewpointMax', value: newMaxDew, unit: state?.tempUnit, descriptionText: "Comfort Dewpoint Maximum is ${newMaxDew}" , displayed: true, isStateChange: true)
	} else {
		//LogAction("Comfort Dewpoint is (${newMinDew}) | Original Minimum Dewpoint: (${curMinDew})")
		LogAction("Comfort Dewpoint Maximum is (${newMaxDew}) | Original Maximum Dewpoint: (${curMaxDew})")
	}
}

def onlineStatusEvent(online) {
	def isOn = device.currentState("onlineStatus")?.value
	def val = online ? "Online" : "Offline"
	state?.onlineStatus = val
	if(!isOn.equals(val)) {
		Logger("UPDATED | Online Status is: (${val}) | Original State: (${isOn})")
		sendEvent(name: "onlineStatus", value: val, descriptionText: "Online Status is: ${val}", displayed: true, isStateChange: true, state: val)
	} else { LogAction("Online Status is: (${val}) | Original State: (${isOn})") }
}

def apiStatusEvent(issue) {
	def curStat = device.currentState("apiStatus")?.value
	def newStat = issue ? "Has Issue" : "Good"
	state?.apiStatus = newStat
	if(!curStat.equals(newStat)) {
		Logger("UPDATED | API Status is: (${newStat.toString().capitalize()}) | Original State: (${curStat.toString().capitalize()})")
		sendEvent(name: "apiStatus", value: newStat, descriptionText: "API Status is: ${newStat}", displayed: true, isStateChange: true, state: newStat)
	} else { LogAction("API Status is: (${newStat}) | Original State: (${curStat})") }
}

def nestReportStatusEvent() {
	def val = currentNestReportData?.toString()
	def rprtData = getNestMgrReport()?.toString()
	if(!val || (val && rprtData && !val.equals(rprtData))) {
		Logger("UPDATED | Current Nest Report Data has been updated", "info")
		sendEvent(name: 'nestReportData', value: rprtData, descriptionText: "Nest Report Data has been updated...", display: false, displayed: false)
	}
}

def canHeatCool(canHeat, canCool) {
	state?.can_heat = !canHeat ? false : true
	state?.can_cool = !canCool ? false : true
	sendEvent(name: "canHeat", value: state?.can_heat.toString())
	sendEvent(name: "canCool", value: state?.can_cool.toString())
}

def hasFan(hasFan) {
	state?.has_fan = (hasFan == "true") ? true : false
	sendEvent(name: "hasFan", value: hasFan.toString())
}

def isEmergencyHeat(val) {
	state?.is_using_emergency_heat = !val ? false : true
}

def clearHeatingSetpoint() {
	sendEvent(name:'heatingSetpoint', value: "",  descriptionText: "Clear Heating Setpoint" , display: false, displayed: true )
	state?.heating_setpoint = ""
}

def clearCoolingSetpoint() {
	sendEvent(name:'coolingSetpoint', value: "",  descriptionText: "Clear Cooling Setpoint" , display: false, displayed: true)
	state?.cooling_setpoint = ""
}

def getCoolTemp() {
	return !device.currentValue("coolingSetpoint") ? 0 : device.currentValue("coolingSetpoint")
}

def getHeatTemp() {
	return !device.currentValue("heatingSetpoint") ? 0 : device.currentValue("heatingSetpoint")
}

def getFanMode() {
	return !device.currentState("thermostatFanMode")?.value ? "unknown" : device.currentState("thermostatFanMode")?.value.toString()
}

def getHvacMode() {
	return !state?.nestHvac_mode ? device.currentState("nestThermostatMode")?.value.toString() : state.nestHvac_mode
	//return !device.currentState("thermostatMode") ? "unknown" : device.currentState("thermostatMode")?.value.toString()
}

def getHvacState() {
	return !device.currentState("thermostatOperatingState") ? "unknown" : device.currentState("thermostatOperatingState")?.value.toString()
}

def getNestPresence() {
	return !state?.nestPresence ? device.currentState("nestPresence")?.value.toString() : state.nestPresence
	//return !device.currentState("nestPresence") ? "home" : device.currentState("nestPresence")?.value.toString()
}

def getPresence() {
	return !device.currentState("presence") ? "present" : device.currentState("presence").value.toString()
}

def getTargetTemp() {
	return !device.currentValue("targetTemperature") ? 0 : device.currentValue("targetTemperature")
}

def getThermostatSetpoint() {
	return !device.currentValue("thermostatSetpoint") ? 0 : device.currentValue("thermostatSetpoint")
}

def getTemp() {
	return !device.currentValue("temperature") ? 0 : device.currentValue("temperature")
}

def getHumidity() {
	return !device.currentValue("humidity") ? 0 : device.currentValue("humidity")
}

def getTempWaitVal() {
	return state?.childWaitVal ? state?.childWaitVal.toInteger() : 4
}

def wantMetric() { return (state?.tempUnit == "C") }


/************************************************************************************************
|							Temperature Setpoint Functions for Buttons							|
*************************************************************************************************/
void heatingSetpointUp() {
	//LogAction("heatingSetpointUp()...", "trace")
	def operMode = getHvacMode()
	if ( operMode in ["heat", "eco", "auto"] ) {
		levelUpDown(1,"heat")
	}
}

void heatingSetpointDown() {
	//LogAction("heatingSetpointDown()...", "trace")
	def operMode = getHvacMode()
	if ( operMode in ["heat","eco", "auto"] ) {
		levelUpDown(-1, "heat")
	}
}

void coolingSetpointUp() {
	//LogAction("coolingSetpointUp()...", "trace")
	def operMode = getHvacMode()
	if ( operMode in ["cool","eco", "auto"] ) {
		levelUpDown(1, "cool")
	}
}

void coolingSetpointDown() {
	//LogAction("coolingSetpointDown()...", "trace")
	def operMode = getHvacMode()
	if ( operMode in ["cool", "eco", "auto"] ) {
		levelUpDown(-1, "cool")
	}
}

void levelUp() {
	levelUpDown(1)
}

void levelDown() {
	levelUpDown(-1)
}

void levelUpDown(tempVal, chgType = null) {
	//LogAction("levelUpDown()...($tempVal | $chgType)", "trace")
	def hvacMode = getHvacMode()

	if (canChangeTemp()) {
	// From RBOY https://community.smartthings.com/t/multiattributetile-value-control/41651/23
	// Determine OS intended behaviors based on value behaviors (urrgghhh.....ST!)
		def upLevel

		if (!state?.lastLevelUpDown) { state.lastLevelUpDown = 0 } // If it isn't defined lets baseline it

		if ((state.lastLevelUpDown == 1) && (tempVal == 1)) { upLevel = true } //Last time it was 1 and again it's 1 its increase

		else if ((state.lastLevelUpDown == 0) && (tempVal == 0)) { upLevel = false } //Last time it was 0 and again it's 0 then it's decrease

		else if ((state.lastLevelUpDown == -1) && (tempVal == -1)) { upLevel = false } //Last time it was -1 and again it's -1 then it's decrease

		else if ((tempVal - state.lastLevelUpDown) > 0) { upLevel = true } //If it's increasing then it's up

		else if ((tempVal - state.lastLevelUpDown) < 0) { upLevel = false } //If it's decreasing then it's down

		else { log.error "UNDEFINED STATE, CONTACT DEVELOPER. Last level $state.lastLevelUpDown, Current level, $value" }

		state.lastLevelUpDown = tempVal // Save it

		def targetVal = 0.0
		def tempUnit = device.currentValue('temperatureUnit')
		def curHeatpoint = device.currentValue("heatingSetpoint")
		def curCoolpoint = device.currentValue("coolingSetpoint")
		def curThermSetpoint = device.latestValue("thermostatSetpoint")
		targetVal = curThermSetpoint ?: 0.0
		if (hvacMode == "auto") {
			if (chgType == "cool") {
				targetVal = curCoolpoint
				curThermSetpoint = targetVal
			}
			if (chgType == "heat") {
				targetVal = curHeatpoint
				curThermSetpoint = targetVal
			}
		}

		if (upLevel) {
			//LogAction("Increasing by 1 increment")
			if (tempUnit == "C" ) {
				targetVal = targetVal.toDouble() + 0.5
				if (targetVal < 9.0) { targetVal = 9.0 }
				if (targetVal > 32.0 ) { targetVal = 32.0 }
			} else {
				targetVal = targetVal.toDouble() + 1.0
				if (targetVal < 50.0) { targetVal = 50 }
				if (targetVal > 90.0) { targetVal = 90 }
			}
		} else {
			//LogAction("Reducing by 1 increment")
			if (tempUnit == "C" ) {
				targetVal = targetVal.toDouble() - 0.5
				if (targetVal < 9.0) { targetVal = 9.0 }
				if (targetVal > 32.0 ) { targetVal = 32.0 }
			} else {
				targetVal = targetVal.toDouble() - 1.0
				if (targetVal < 50.0) { targetVal = 50 }
				if (targetVal > 90.0) { targetVal = 90 }
			}
		}

		if (targetVal != curThermSetpoint ) {
			pauseEvent("true")
			switch (hvacMode) {
				case "heat":
					Logger("Sending changeSetpoint(Temp: ${targetVal})")
					if (state?.oldHeat == null) { state.oldHeat = curHeatpoint}
					thermostatSetpointEvent(targetVal)
					heatingSetpointEvent(targetVal)
					if (!chgType) { chgType = "" }
//						runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
					scheduleChangeSetpoint()
					break
				case "cool":
					Logger("Sending changeSetpoint(Temp: ${targetVal})")
					if (state?.oldCool == null) { state.oldCool = curCoolpoint}
					thermostatSetpointEvent(targetVal)
					coolingSetpointEvent(targetVal)
					if (!chgType) { chgType = "" }
//						runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
					scheduleChangeSetpoint()
					break
				case "auto":
					if (chgType) {
						switch (chgType) {
							case "cool":
								Logger("Sending changeSetpoint(Temp: ${targetVal})")
								if (state?.oldCool == null) { state.oldCool = curCoolpoint}
								coolingSetpointEvent(targetVal)
//									runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
								scheduleChangeSetpoint()
								break
							case "heat":
								Logger("Sending changeSetpoint(Temp: ${targetVal})")
								if (state?.oldHeat == null) { state.oldHeat = curHeatpoint}
								heatingSetpointEvent(targetVal)
//									runIn( getTempWaitVal(), "changeSetpoint", [data: [temp:targetVal, mode:chgType], overwrite: true] )
								scheduleChangeSetpoint()
								break
							default:
								Logger("Can not change temp while in this mode ($chgType}!!!", "warn")
								break
						}
					} else { Logger("Temp Change without a chgType is not supported!!!", "warn") }
					break
				default:
					pauseEvent("false")
					Logger("Unsupported Mode Received: ($hvacMode}!!!", "warn")
					break
			}
		}
	} else { Logger("levelUpDown: Cannot adjust temperature due to presence: ${state?.present} or hvacMode ${hvacMode}") }
}

def scheduleChangeSetpoint() {
	if (getLastChangeSetpointSec() > 15) {
		state?.lastChangeSetpointDt = getDtNow()
		runIn( 25, "changeSetpoint", [overwrite: true] )
	}
}

def getLastChangeSetpointSec() { return !state?.lastChangeSetpointDt ? 100000 : GetTimeDiffSeconds(state?.lastChangeSetpointDt).toInteger() }

def getDtNow() {
	def now = new Date()
	return formatDt(now)
}

def formatDt(dt) {
	def tf = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy")
	if(getTimeZone()) { tf.setTimeZone(getTimeZone()) }
	else {
		Logger("SmartThings TimeZone is not found or is not set... Please Try to open your ST location and Press Save...", "warn")
	}
	return tf.format(dt)
}


//Returns time differences is seconds
def GetTimeDiffSeconds(lastDate) {
	if(lastDate?.contains("dtNow")) { return 10000 }
	def now = new Date()
	def lastDt = Date.parse("E MMM dd HH:mm:ss z yyyy", lastDate)
	def start = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(lastDt)).getTime()
	def stop = Date.parse("E MMM dd HH:mm:ss z yyyy", formatDt(now)).getTime()
	def diff = (int) (long) (stop - start) / 1000
	return diff
}

// Nest does not allow temp changes in away modes
def canChangeTemp() {
	//LogAction("canChangeTemp()...", "trace")
	def curPres = getNestPresence()
	if (curPres == "home" && state?.nestHvac_mode != "eco") {
		def hvacMode = getHvacMode()
		switch (hvacMode) {
			case "heat":
				return true
				break
			case "cool":
				return true
				break
			case "auto":
				return true
				break
			default:
				return false
				break
		}
	} else { return false }
}

void changeSetpoint() {
	//LogAction("changeSetpoint()... ($val)", "trace")
	try {
		if ( canChangeTemp() ) {

//			def temp = val?.temp?.value.toDouble()
//			def md = !val?.mode?.value ? null : val?.mode?.value

			def md
			def hvacMode = getHvacMode()
			def curHeatpoint = getHeatTemp()
			def curCoolpoint = getCoolTemp()

			LogAction("changeSetpoint()... hvacMode: ${hvacMode} curHeatpoint: ${curHeatpoint}  curCoolpoint: ${curCoolpoint} oldCool: ${state?.oldCool} oldHeat: ${state?.oldHeat}", "trace")

			switch (hvacMode) {
				case "heat":
					state.oldHeat = null
					setHeatingSetpoint(curHeatpoint)
					break
				case "cool":
					state.oldCool = null
					setCoolingSetpoint(curCoolpoint)
					break
				case "auto":
					if ( (state?.oldCool != null) && (state?.oldHeat == null) ) { md = "cool"}
					if ( (state?.oldCool == null) && (state?.oldHeat != null) ) { md = "heat"}
					if ( (state?.oldCool != null) && (state?.oldHeat != null) ) { md = "both"}

					def heatFirst
					if(md) {
						if (curHeatpoint >= curCoolpoint) {
							Logger("changeSetpoint: Invalid Temp Type received in auto mode... ${curHeatpoint} ${curCoolpoint}", "warn")
						} else {
							if("${md}" == "heat") { state.oldHeat = null; setHeatingSetpoint(curHeatpoint) }
							else if ("${md}" == "cool") { state.oldCool = null; setCoolingSetpoint(curCoolpoint) }
							else if ("${md}" == "both") {
								if (curHeatpoint <= state.oldHeat) { heatfirst = true }
								else if (curCoolpoint >= state.oldCool) { heatFirst = false }
								else if (curHeatpoint > state.oldHeat) { heatFirst = false }
								else { heatFirst = true }
								if (heatFirst) {
									state.oldHeat = null
									setHeatingSetpoint(curHeatpoint)
									state.oldCool = null
									setCoolingSetpoint(curCoolpoint)
								} else {
									state.oldCool = null
									setCoolingSetpoint(curCoolpoint)
									state.oldHeat = null
									setHeatingSetpoint(curHeatpoint)
								}
							}
						}
					} else {
						Logger("changeSetpoint: Invalid Temp Type received... ${md}", "warn")
						state.oldCool = null
						state.oldHeat = null
					}
					break
				default:
					if (curHeatpoint > curCoolpoint) {
						Logger("changeSetpoint: Invalid Temp Type received in auto mode... ${curHeatpoint} ${curCoolpoint} ${val}", "warn")
					}
					//thermostatSetpointEvent(temp)
					break
			}
		}
		pauseEvent("false")
	}
	catch (ex) {
		pauseEvent("false")
		log.error "changeSetpoint Exception:", ex
		exceptionDataHandler(ex.message, "changeSetpoint")
	}
}

// Nest Only allows F temperatures as #.0  and C temperatures as either #.0 or #.5
void setHeatingSetpoint(temp) {
	setHeatingSetpoint(temp.toDouble())
}

void setHeatingSetpoint(Double reqtemp) {
	try {
		LogAction("setHeatingSetpoint()... ($reqtemp)", "trace")
		def hvacMode = getHvacMode()
		def tempUnit = state?.tempUnit
		def temp = 0.0
		def canHeat = state?.can_heat.toBoolean()
		def result = false

		LogAction("Heat Temp Received: ${reqtemp} (${tempUnit})")
		if (state?.present && canHeat && state?.nestHvac_mode != "eco") {
			switch (tempUnit) {
				case "C":
					temp = Math.round(reqtemp.round(1) * 2) / 2.0f
					if (temp) {
						if (temp < 9.0) { temp = 9.0 }
						if (temp > 32.0 ) { temp = 32.0 }
						LogAction("Sending Heat Temp ($temp)")
						if (hvacMode == 'auto') {
							parent.setTargetTempLow(this, tempUnit, temp, virtType())
							heatingSetpointEvent(temp)
						}
						if (hvacMode == 'heat') {
							parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
							thermostatSetpointEvent(temp)
							heatingSetpointEvent(temp)
						}
					}
					result = true
					break
				case "F":
					temp = reqtemp.round(0).toInteger()
					if (temp) {
						if (temp < 50) { temp = 50 }
						if (temp > 90) { temp = 90 }
						LogAction("Sending Heat Temp ($temp)")
						if (hvacMode == 'auto') {
							parent.setTargetTempLow(this, tempUnit, temp, virtType())
							heatingSetpointEvent(temp)
						}
						if (hvacMode == 'heat') {
							parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
							thermostatSetpointEvent(temp)
							heatingSetpointEvent(temp)
						}
					}
					result = true
					break
				default:
					Logger("no Temperature data $tempUnit")
				break
			}
		} else {
			Logger("Skipping heat change")
			result = false
		}
	}
	catch (ex) {
		log.error "setHeatingSetpoint Exception:", ex
		exceptionDataHandler(ex.message, "setHeatingSetpoint")
	}
}

void setCoolingSetpoint(temp) {
	setCoolingSetpoint( temp.toDouble() )
}

void setCoolingSetpoint(Double reqtemp) {
	try {
		LogAction("setCoolingSetpoint()... ($reqtemp)", "trace")
		def hvacMode = getHvacMode()
		def temp = 0.0
		def tempUnit = state?.tempUnit
		def canCool = state?.can_cool.toBoolean()
		def result = false

		LogAction("Cool Temp Received: ${reqtemp} (${tempUnit})")
		if (state?.present && canCool && state?.nestHvac_mode != "eco") {
			switch (tempUnit) {
				case "C":
					temp = Math.round(reqtemp.round(1) * 2) / 2.0f
					if (temp) {
						if (temp < 9.0) { temp = 9.0 }
						if (temp > 32.0) { temp = 32.0 }
						LogAction("Sending Cool Temp ($temp)")
						if (hvacMode == 'auto') {
							parent.setTargetTempHigh(this, tempUnit, temp, virtType())
							coolingSetpointEvent(temp)
						}
						if (hvacMode == 'cool') {
							parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
							thermostatSetpointEvent(temp)
							coolingSetpointEvent(temp)
						}
					}
					result = true
					break

				case "F":
					temp = reqtemp.round(0).toInteger()
					if (temp) {
						if (temp < 50) { temp = 50 }
						if (temp > 90) { temp = 90 }
						LogAction("Sending Cool Temp ($temp)")
						if (hvacMode == 'auto') {
							parent.setTargetTempHigh(this, tempUnit, temp, virtType())
							coolingSetpointEvent(temp)
						}
						if (hvacMode == 'cool') {
							parent.setTargetTemp(this, tempUnit, temp, hvacMode, virtType())
							thermostatSetpointEvent(temp)
							coolingSetpointEvent(temp)
						}
					}
					result = true
					break
				default:
						Logger("no Temperature data $tempUnit")
					break
			}
		} else {
			Logger("Skipping cool change")
			result = false
		}
	}
	catch (ex) {
		log.error "setCoolingSetpoint Exception:", ex
		exceptionDataHandler(ex.message, "setCoolingSetpoint")
	}
}

/************************************************************************************************
|									NEST PRESENCE FUNCTIONS										|
*************************************************************************************************/
void setPresence() {
	try {
		LogAction("setPresence()...", "trace")
		def pres = getNestPresence()
		LogAction("Current Nest Presence: ${pres}", "trace")
		if(pres == "auto-away" || pres == "away") {
			if (parent.setStructureAway(this, "false", virtType())) { presenceEvent("home") }
		}
		else if (pres == "home") {
			if (parent.setStructureAway(this, "true", virtType())) { presenceEvent("away") }
		}
	}
	catch (ex) {
		log.error "setPresence Exception:", ex
		exceptionDataHandler(ex.message, "setPresence")
	}
}

// backward compatibility for previous nest thermostat (and rule machine)
void away() {
	try {
		LogAction("away()...", "trace")
		setAway()
	}
	catch (ex) {
		log.error "away Exception:", ex
		exceptionDataHandler(ex.message, "away")
	}
}

// backward compatibility for previous nest thermostat (and rule machine)
void present() {
	try {
		LogAction("present()...", "trace")
		setHome()
	}
	catch (ex) {
		log.error "present Exception:", ex
		exceptionDataHandler(ex.message, "present")
	}
}

def setAway() {
	try {
		LogAction("setAway()...", "trace")
		if (parent.setStructureAway(this, "true", virtType())) { presenceEvent("away") }
	}
	catch (ex) {
		log.error "setAway Exception:", ex
		exceptionDataHandler(ex.message, "setAway")
	}
}

def setHome() {
	try {
		LogAction("setHome()...", "trace")
		if (parent.setStructureAway(this, "false", virtType()) ) { presenceEvent("home") }
	}
	catch (ex) {
		log.error "setHome Exception:", ex
		exceptionDataHandler(ex.message, "setHome")
	}
}

/************************************************************************************************
|										HVAC MODE FUNCTIONS										|
************************************************************************************************/

def getHvacModes() {
	//LogAction("Building Modes list")
	def modesList = ['off']
	if( state?.can_heat == true ) { modesList.push('heat') }
	if( state?.can_cool == true ) { modesList.push('cool') }
	if( state?.can_heat == true && state?.can_cool == true ) { modesList.push('auto') }
	modesList.push('eco')
	LogAction("Modes = ${modesList}")
	return modesList
}

def changeMode() {
	try {
		//LogAction("changeMode..")
		def currentMode = getHvacMode()
		def lastTriedMode = currentMode ?: "off"
		def modeOrder = getHvacModes()
		def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
		def nextMode = next(lastTriedMode)
		LogAction("changeMode() currentMode: ${currentMode}   lastTriedMode:  ${lastTriedMode}  modeOrder:  ${modeOrder}   nextMode: ${nextMode}", "trace")
		setHvacMode(nextMode)
	}
	catch (ex) {
		log.error "changeMode Exception:", ex
		exceptionDataHandler(ex.message, "changeMode")
	}
}

def setHvacMode(nextMode) {
	try {
		LogAction("setHvacMode(${nextMode})")
		if (nextMode in getHvacModes()) {
			state.lastTriedMode = nextMode
			"$nextMode"()
		} else {
			Logger("Invalid Mode '$nextMode'")
		}
	}
	catch (ex) {
		log.error "setHvacMode Exception:", ex
		exceptionDataHandler(ex.message, "setHvacMode")
	}
}

def doChangeMode() {
	try {
		def currentMode = device.currentState("nestThermostatMode")?.value
		LogAction("doChangeMode()  currentMode:  ${currentMode}")
		def errflag = true
		switch(currentMode) {
			case "auto":
				if (parent.setHvacMode(this, "heat-cool", virtType())) {
					errflag = false
				}
				break
			case "heat":
				if (parent.setHvacMode(this, "heat", virtType())) {
					errflag = false
				}
				break
			case "cool":
				if (parent.setHvacMode(this, "cool", virtType())) {
					errflag = false
				}
				break
			case "off":
				if (parent.setHvacMode(this, "off", virtType())) {
					errflag = false
				}
				break
			case "eco":
				if (parent.setHvacMode(this, "eco", virtType())) {
					errflag = false
				}
				break
			default:
				Logger("doChangeMode Received an Invalid Request: ${currentMode}", "warn")
				break
		}
		if (errflag) {
			Logger("doChangeMode call to change mode failed: ${currentMode}", "warn")
			refresh()
		}
	}
	catch (ex) {
		log.error "doChangeMode Exception:", ex
		exceptionDataHandler(ex.message, "doChangeMode")
	}
}

void off() {
	LogAction("off()...", "trace")
	hvacModeEvent("off")
	doChangeMode()
}

void heat() {
	LogAction("heat()...", "trace")
	hvacModeEvent("heat")
	doChangeMode()
}

void emergencyHeat() {
	LogAction("emergencyHeat()...", "trace")
	Logger("Emergency Heat setting not allowed", "warn")
}

void cool() {
	LogAction("cool()...", "trace")
	hvacModeEvent("cool")
	doChangeMode()
}

void auto() {
	LogAction("auto()...", "trace")
	hvacModeEvent("auto")
	doChangeMode()
}

void eco() {
	LogAction("eco()...", "trace")
	hvacModeEvent("eco")
	doChangeMode()
}

void setThermostatMode(modeStr) {
	LogAction("setThermostatMode()...", "trace")
	switch(modeStr) {
		case "auto":
			auto()
			break
		case "heat":
			heat()
			break
		case "cool":
			cool()
			break
		case "eco":
			eco()
			break
		case "off":
			off()
			break
		case "emergency heat":
			emergencyHeat()
			break
		default:
			Logger("setThermostatMode Received an Invalid Request: ${modeStr}", "warn")
			break
	}
}


/************************************************************************************************
|										FAN MODE FUNCTIONS										|
*************************************************************************************************/
void fanOn() {
	try {
		LogAction("fanOn()...", "trace")
		if ( state?.has_fan.toBoolean() ) {
			if (parent.setFanMode(this, true, virtType()) ) { fanModeEvent("true") }
		} else { Logger("Error setting fanOn", "error") }
	}
	catch (ex) {
		log.error "fanOn Exception:", ex
		exceptionDataHandler(ex.message, "fanOn")
	}
}

// non standard by ST Capabilities Thermostat Fan Mode
void fanOff() {
	LogAction("fanOff()...", "trace")
	fanAuto()
}

void fanCirculate() {
	LogAction("fanCirculate()...", "trace")
	fanOn()
}

void fanAuto() {
	try {
		LogAction("fanAuto()...", "trace")
		if ( state?.has_fan.toBoolean() ) {
			if (parent.setFanMode(this,false, virtType()) ) { fanModeEvent("false") }
		} else { Logger("Error setting fanAuto", "error") }
	}
	catch (ex) {
		log.error "fanAuto Exception:", ex
		exceptionDataHandler(ex.message, "fanAuto")
	}
}

void setThermostatFanMode(fanModeStr) {
	LogAction("setThermostatFanMode()... ($fanModeStr)", "trace")
	switch(fanModeStr) {
		case "auto":
			fanAuto()
			break
		case "on":
			fanOn()
			break
		case "circulate":
			fanCirculate()
			break
		case "off":   // non standard by ST Capabilities Thermostat Fan Mode
			fanOff()
			break
		default:
			Logger("setThermostatFanMode Received an Invalid Request: ${fanModeStr}", "warn")
			break
	}
}


/**************************************************************************
|												LOGGING FUNCTIONS												  |
***************************************************************************/

void Logger(msg, logType = "debug") {
	def smsg = state?.showLogNamePrefix ? "${device.displayName}: ${msg}" : "${msg}"
	switch (logType) {
		case "trace":
			log.trace "${smsg}"
			break
		case "debug":
			log.debug "${smsg}"
			break
		case "info":
			log.info "${smsg}"
			break
		case "warn":
			log.warn "${smsg}"
			break
		case "error":
			log.error "${smsg}"
			break
		default:
			log.debug "${smsg}"
			break
	}
}

// Local Application Logging
void LogAction(msg, logType = "debug") {
	if(state?.debug) {
		Logger(msg, logType)
	}
}

//This will Print logs from the parent app when added to parent method that the child calls
def log(message, level = "trace") {
	def smsg = "PARENT_Log>> " + message
	LogAction(smsg, level)
	return null // always child interface call with a return value
}

def exceptionDataHandler(msg, methodName) {
	if(state?.allowDbException == false) {
		return
	} else {
		if(msg && methodName) {
			def msgString = "${msg}"
			parent?.sendChildExceptionData("thermostat", devVer(), msgString, methodName)
		}
	}
}

/**************************************************************************
|					  HTML TILE RENDER FUNCTIONS	  					  |
***************************************************************************/

def getImgBase64(url,type) {
	try {
		def params = [
			uri: url,
			contentType: 'image/$type'
		]
		httpGet(params) { resp ->
			if(resp.data) {
				def respData = resp?.data
				ByteArrayOutputStream bos = new ByteArrayOutputStream()
				int len
				int size = 3072
				byte[] buf = new byte[size]
				while ((len = respData.read(buf, 0, size)) != -1)
					bos.write(buf, 0, len)
				buf = bos.toByteArray()
				//LogAction("buf: $buf")
				String s = buf?.encodeBase64()
				//LogAction("resp: ${s}")
				return s ? "data:image/${type};base64,${s.toString()}" : null
			}
		}
	}
	catch (ex) {
		log.error "getImageBytes Exception:", ex
		exceptionDataHandler(ex.message, "getImgBase64")
	}
}

def getFileBase64(url,preType,fileType) {
	try {
		def params = [
			uri: url,
			contentType: '$preType/$fileType'
		]
		httpGet(params) { resp ->
			if(resp.data) {
				def respData = resp?.data
				ByteArrayOutputStream bos = new ByteArrayOutputStream()
				int len
				int size = 4096
				byte[] buf = new byte[size]
				while ((len = respData.read(buf, 0, size)) != -1)
					bos.write(buf, 0, len)
				buf = bos.toByteArray()
				//LogAction("buf: $buf")
				String s = buf?.encodeBase64()
				//LogAction("resp: ${s}")
				return s ? "data:${preType}/${fileType};base64,${s.toString()}" : null
			}
		}
	}
	catch (ex) {
		log.error "getFileBase64 Exception:", ex
		exceptionDataHandler(ex.message, "getFileBase64")
	}
}

def getCSS(url = null){
	try {
		def params = [
			uri: !url ? cssUrl() : url?.toString(),
			contentType: 'text/css'
		]
		httpGet(params)  { resp ->
			return resp?.data.text
		}
	}
	catch (ex) {
		log.error "getCss Exception:", ex
		exceptionDataHandler(ex.message, "getCSS")
	}
}

def getJS(url){
	def params = [
		uri: url?.toString(),
		contentType: "text/plain"
	]
	httpGet(params)  { resp ->
		return resp?.data.text
	}
}

def getCssData() {
	def cssData = null
	def htmlInfo
	state.cssData = null

	if(htmlInfo?.cssUrl && htmlInfo?.cssVer) {
		if(state?.cssData) {
			if (state?.cssVer?.toInteger() == htmlInfo?.cssVer?.toInteger()) {
				//LogAction("getCssData: CSS Data is Current | Loading Data from State...")
				cssData = state?.cssData
			} else if (state?.cssVer?.toInteger() < htmlInfo?.cssVer?.toInteger()) {
				//LogAction("getCssData: CSS Data is Outdated | Loading Data from Source...")
				cssData = getFileBase64(htmlInfo.cssUrl, "text", "css")
				state.cssData = cssData
				state?.cssVer = htmlInfo?.cssVer
			}
		} else {
			//LogAction("getCssData: CSS Data is Missing | Loading Data from Source...")
			cssData = getFileBase64(htmlInfo.cssUrl, "text", "css")
			state?.cssData = cssData
			state?.cssVer = htmlInfo?.cssVer
		}
	} else {
		//LogAction("getCssData: No Stored CSS Info Data Found for Device... Loading for Static URL...")
		cssData = getFileBase64(cssUrl(), "text", "css")
	}
	return cssData
}

def getChartJsData() {
	def chartJsData = null
	//def htmlInfo = state?.htmlInfo
	def htmlInfo
	state.chartJsData = null

	if(htmlInfo?.chartJsUrl && htmlInfo?.chartJsVer) {
		if(state?.chartJsData) {
			if (state?.chartJsVer?.toInteger() == htmlInfo?.chartJsVer?.toInteger()) {
				//LogAction("getChartJsData: Chart Javascript Data is Current | Loading Data from State...")
				chartJsData = state?.chartJsData
			} else if (state?.chartJsVer?.toInteger() < htmlInfo?.chartJsVer?.toInteger()) {
				//LogAction("getChartJsData: Chart Javascript Data is Outdated | Loading Data from Source...")
				//chartJsData = getFileBase64(htmlInfo.chartJsUrl, "text", "javascript")
				state.chartJsData = chartJsData
				state?.chartJsVer = htmlInfo?.chartJsVer
			}
		} else {
			//LogAction("getChartJsData: Chart Javascript Data is Missing | Loading Data from Source...")
			chartJsData = getFileBase64(htmlInfo.chartJsUrl, "text", "javascript")
			state?.chartJsData = chartJsData
			state?.chartJsVer = htmlInfo?.chartJsVer
		}
	} else {
		//LogAction("getChartJsData: No Stored Chart Javascript Data Found for Device... Loading for Static URL...")
		chartJsData = getFileBase64(chartJsUrl(), "text", "javascript")
	}
	return chartJsData
}

def cssUrl()	 { return "https://raw.githubusercontent.com/desertblade/ST-HTMLTile-Framework/master/css/smartthings.css" }
def chartJsUrl() { return "https://www.gstatic.com/charts/loader.js" }

def getImg(imgName) {
	return imgName ? "https://cdn.rawgit.com/tonesto7/nest-manager/master/Images/Devices/$imgName" : ""
}

/*
	 variable	  attribute for history	   getRoutine			 variable is present

   temperature		   "temperature"		getTemp				   true						 #
   coolSetpoint		 "coolingSetpoint"		getCoolTemp		   state.can_cool				   #
   heatSetpoint		 "heatingSetpoint"		getHeatTemp		   state.can_heat				   #
   operatingState	 "thermostatOperatingState"	getHvacState			true				 idle cooling heating
   operatingMode	"thermostatMode"		getHvacMode			true				 heat cool off auto
	presence	   "presence"			getPresence			true				 present  not present
*/

String getDataString(Integer seriesIndex) {
	//LogAction("getDataString ${seriesIndex}", "trace")
	def dataTable = []
	switch (seriesIndex) {
		case 1:
			dataTable = state?.temperatureTableYesterday
			break
		case 2:
			dataTable = state?.temperatureTable
			break
		case 3:
			dataTable = state?.operatingStateTable
			break
		case 4:
			dataTable = state?.humidityTable
			break
		case 5:
			dataTable = state?.coolSetpointTable
			break
		case 6:
			dataTable = state?.heatSetpointTable
			break
		case 7:
			dataTable = state?.extTempTable
			break
		case 8:
			dataTable = state?.fanModeTable
			break
	}

	def lastVal = 200

	//LogAction("getDataString ${seriesIndex} ${dataTable}")
	//LogAction("getDataString ${seriesIndex}")

	def lastAdded = false
	def dataArray
	def myval
	def myindex
	def lastdataArray = null
	def dataString = ""

	if (seriesIndex == 5) {
	  // state.can_cool
	}
	if (seriesIndex == 6) {
	   // state.can_heat
	}
	if (seriesIndex == 8) {
		//state?.has_fan
	}
	def myhas_fan = state?.has_fan && false ? true : false    // false because not graphing fan operation now

	def has_weather = false
	if(state?.curExtTemp != null) { has_weather = true }

	def datacolumns

	myindex = seriesIndex
//ERSERS
	datacolumns = 8
	//if (state?.can_heat && state?.can_cool && myhas_fan && has_weather) { datacolumns = 8 }
	if (!myhas_fan) {
		datacolumns -= 1
	}
	if (!has_weather) {
		datacolumns -= 1
		if (myindex == 8) { myindex = 7 }
	}
	if ((!state?.can_heat && state?.can_cool) || (state?.can_heat && !state?.can_cool)) {
		datacolumns -= 1
		if (myindex >= 6) { myindex -= 1 }
	}
	switch (datacolumns) {
		case 8:
			dataArray = [[0,0,0],null,null,null,null,null,null,null,null]
			break
		case 7:
			dataArray = [[0,0,0],null,null,null,null,null,null,null]
			break
		case 6:
			dataArray = [[0,0,0],null,null,null,null,null,null]
			break
		case 5:
			dataArray = [[0,0,0],null,null,null,null,null]
			break
		default:
			LogAction("getDataString: bad column result", "error")
	}

	//dataTable.each() {

	dataTable.any { it ->
		myval = it[2]

		//convert idle / non-idle to numeric value
		if (myindex == 3) {
			if(myval == "idle") { myval = 0 }
			if(myval == "cooling") { myval = 8 }
			if(myval == "heating") { myval = 16 }
			//else { }
		}
/*
		if(myhas_fan && seriesIndex == 8) {
			if(myval == "auto") { myval = 0 }
			if(myval == "on") { myval = 8 }
			//if (myval == "circulate") { myval = 8 }
		}
*/
		if (seriesIndex == 5) {
			if(myval == 0) { return false }
		// state.can_cool
		}
		if (seriesIndex == 6) {
			if(myval == 0) { return false }
		// state.can_heat
		}

		dataArray[myindex] = myval
		dataArray[0] = [it[0],it[1],0]

/*
		//reduce # of points to graph
		if (lastVal != myval) {
			lastAdded = true
			if (lastdataArray) {   //controls curves
				dataString += lastdataArray?.toString() + ","
			}
			lastdataArray = null
			lastVal = myval
			dataString += dataArray?.toString() + ","
		} else { lastAdded = false; lastdataArray = dataArray }
*/
		dataString += dataArray?.toString() + ","
		return false
	}

/*
	if (!lastAdded && dataString) {
		dataArray[myindex] = myval
		dataString += dataArray?.toString() + ","
	}
*/

	if (dataString == "") {
		dataArray[0] = [0,0,0]
		//dataArray[myindex] = 0
		dataString += dataArray?.toString() + ","
	}

	//LogAction("getDataString ${seriesIndex} datacolumns: ${datacolumns}  myindex: ${myindex} datastring: ${dataString}")
	return dataString
}

def tgetSomeOldData(val) {
	LogAction("tgetSomeOldData ${val}", "trace")
	def type = val?.type?.value
	def attributestr  = val?.attributestr?.value
	def gfloat = val?.gfloat?.value
	def devpoll = val?.devpoll?.value
	LogAction("calling getSomeOldData ( ${type}, ${attributestr}, ${gfloat}, ${devpoll})", "trace")
	getSomeOldData(type, attributestr, gfloat, devpoll)
}

def getSomeOldData(type, attributestr, gfloat, devpoll = false, nostate = true) {
	LogAction("getSomeOldData ( ${type}, ${attributestr}, ${gfloat}, ${devpoll})", "trace")

//	if (devpoll && (!state?."${type}TableYesterday" || !state?."${type}Table")) {
//		runIn( 66, "tgetSomeOldData", [data: [type:type, attributestr:attributestr, gfloat:gfloat, devpoll:false]])
//		return
//	}

	def startOfToday = timeToday("00:00", location.timeZone)
	def newValues
	def dataTable = []

	if (( nostate || state?."${type}TableYesterday" == null) && attributestr ) {
		LogAction("Querying DB for yesterday's ${type} data…", "trace")
		def yesterdayData = device.statesBetween("${attributestr}", startOfToday - 1, startOfToday, [max: 100])
		LogAction("got ${yesterdayData.size()}")
		if (yesterdayData.size() > 0) {
			while ((newValues = device.statesBetween("${attributestr}", startOfToday - 1, yesterdayData.last().date, [max: 100])).size()) {
				LogAction("got ${newValues.size()}")
				yesterdayData += newValues
			}
		}
		LogAction("got ${yesterdayData.size()}")
		dataTable = []
		yesterdayData.reverse().each() {
			if (gfloat) { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.floatValue]) }
			else { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.stringValue]) }
		}
		LogAction("finished ${dataTable}")
		if (!nostate) {
			state."${type}TableYesterday" = dataTable
		}
	}

	if ( nostate || state?."${type}Table" == null) {
		LogAction("Querying DB for today's ${type} data…", "trace")
		def todayData = device.statesSince("${attributestr}", startOfToday, [max: 100])
		LogAction("got ${todayData.size()}")
		if (todayData.size() > 0) {
			while ((newValues = device.statesBetween("${attributestr}", startOfToday, todayData.last().date, [max: 100])).size()) {
				LogAction("got ${newValues.size()}")
				todayData += newValues
			}
		}
		LogAction("got ${todayData.size()}")
		dataTable = []
		todayData.reverse().each() {
			if (gfloat) { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.floatValue]) }
			else { dataTable.add([it.date.format("H", location.timeZone),it.date.format("m", location.timeZone),it.stringValue]) }
		}
		LogAction("finished ${dataTable}")
		if (!nostate) {
			state."${type}Table" = dataTable
		}
	}
}

void getSomeData(devpoll = false) {
	//LogAction("getSomeData ${app}", "trace")

// hackery to test getting old data
	def tryNum = 1
	if (state.eric != tryNum ) {
		if (devpoll) {
			runIn( 33, "getSomeData", [overwrite: true])
			return
		}

		runIn( 33, "getSomeData", [overwrite: true])
		state.eric = tryNum

		state.temperatureTableYesterday = null
		state.operatingStateTableYesterday = null
		state.humidityTableYesterday = null
		state.coolSetpointTableYesterday = null
		state.heatSetpointTableYesterday = null

		state.temperatureTable = null
		state.operatingStateTable = null
		state.humidityTable = null
		state.coolSetpointTable = null
		state.heatSetpointTable = null

		state.remove("temperatureTableYesterday")
		state.remove("operatingStateTableYesterday")
		state.remove("humidityTableYesterday")
		state.remove("coolSetpointTableYesterday")
		state.remove("heatSetpointTableYesterday")

		state.remove("today")
		state.remove("temperatureTable")
		state.remove("operatingStateTable")
		state.remove("humidityTable")
		state.remove("coolSetpointTable")
		state.remove("heatSetpointTable")

		return
	} else {
		//getSomeOldData("temperature", "temperature", true, devpoll)
		//getSomeOldData("operatingState", "thermostatOperatingState", false, devpoll)
		//getSomeOldData("humidity", "humidity", false, devpoll)
		//if (state?.can_cool) { getSomeOldData("coolSetpoint", "coolingSetpoint", true, devpoll) }
		//if (state?.can_heat) { getSomeOldData("heatSetpoint", "heatingSetpoint", true, devpoll) }
	}

	def today = new Date()
	def todayDay = today.format("dd",location.timeZone)

	if (state?.temperatureTable == null) {

	// these are commented out as the platform continuously times out
		//getSomeOldData("temperature", "temperature", true, devpoll)
		//getSomeOldData("operatingState", "thermostatOperatingState", false, devpoll)
		//getSomeOldData("humidity", "humidity", false, devpoll)
		//if (state?.can_cool) { getSomeOldData("coolSetpoint", "coolingSetpoint", true, devpoll) }
		//if (state?.can_heat) { getSomeOldData("heatSetpoint", "heatingSetpoint", true, devpoll) }

		state.temperatureTable = []
		state.operatingStateTable = []
		state.humidityTable = []
		state.coolSetpointTable = []
		state.heatSetpointTable = []
		state.extTempTable = []
		state.fanModeTable = []
		addNewData()
	}

	def temperatureTable = state?.temperatureTable
	def operatingStateTable = state?.operatingStateTable
	def humidityTable = state?.humidityTable
	def coolSetpointTable = state?.coolSetpointTable
	def heatSetpointTable = state?.heatSetpointTable
	def extTempTable = state?.extTempTable
	def fanModeTable = state?.fanModeTable

	if(fanModeTable == null) {		// upgrade cleanup TODO
		state.fanModeTable = []; fanModeTable = state.fanModeTable; state.fanModeTableYesterday = fanModeTable
	}
	if(extTempTable == null) {		// upgrade cleanup TODO
		state.extTempTable = []; extTempTable = state.extTempTable; state.extTempTableYesterday = extTempTable
	}
	def hm = state?.historyStoreMap
	if(hm == null) {
		initHistoryStore()
	}

	if (state?.temperatureTableYesterday?.size() == 0) {
		state.temperatureTableYesterday = temperatureTable
		state.operatingStateTableYesterday = operatingStateTable
		state.humidityTableYesterday = humidityTable
		state.coolSetpointTableYesterday = coolSetpointTable
		state.heatSetpointTableYesterday = heatSetpointTable
		state.extTempTableYesterday = extTempTable
		state.fanModeTableYesterday = fanModeTable
	}

// DAY CHANGE
	if (!state?.today || state.today != todayDay) {
		state.today = todayDay
		state.temperatureTableYesterday = temperatureTable
		state.operatingStateTableYesterday = operatingStateTable
		state.humidityTableYesterday = humidityTable
		state.coolSetpointTableYesterday = coolSetpointTable
		state.heatSetpointTableYesterday = heatSetpointTable
		state.extTempTableYesterday = extTempTable
		state.fanModeTableYesterday = fanModeTable

		state.temperatureTable = []
		state.operatingStateTable = []
		state.humidityTable = []
		state.coolSetpointTable = []
		state.heatSetpointTable = []
		state.extTempTable = []
		state.fanModeTable = []
		updateOperatingHistory(today)

	}
	//initHistoryStore() 	// TODO DEBUGGING
	//updateOperatingHistory(today) // TODO DEBUGGING
	addNewData()
	//def bb = getHistoryStore()   // TODO DEBUGGING
}

def updateOperatingHistory(today) {
	log.trace "updateOperatingHistory()..."

	def dayChange = false
	def monthChange = false
	def yearChange = false

	def hm = state?.historyStoreMap
	if(hm == null) {
		log.error "hm is null"
		return
	}
	def dayNum = today.format("u", location.timeZone).toInteger() // 1 = Monday,... 7 = Sunday
	def monthNum = today.format("MM", location.timeZone).toInteger()
	def yearNum = today.format("YYYY", location.timeZone).toInteger()

	if(hm.currentDay == null) {
		log.error "hm.currentDay is null"
		return
	}

	log.debug "dayNum: ${dayNum} currentDay ${hm.currentDay} | monthNum: ${monthNum} currentMonth ${hm.currentMonth}  | yearNum: ${yearNum} currentYear: ${hm.currentYear}"

	if(dayNum != hm.currentDay) {
		dayChange = true
	}
	if(monthNum != hm.currentMonth) {
		monthChange = true
	}
	if(yearNum != hm.currentYear) {
		yearChange = true
	}

	if(dayChange) {
		def Op_coolingusage = getSumUsage(state.operatingStateTableYesterday, "cooling").toInteger()
		def Op_heatingusage = getSumUsage(state.operatingStateTableYesterday, "heating").toInteger()
		def Op_idle = getSumUsage(state.operatingStateTableYesterday, "idle").toInteger()
		def fan_on = getSumUsage(state.fanModeTableYesterday, "on").toInteger()
		def fan_auto = getSumUsage(state.fanModeTableYesterday, "auto").toInteger()

		log.info "fanon ${fan_on}  fanauto: ${fan_auto} opidle: ${Op_idle}  cool: ${Op_coolingusage} heat: ${Op_heatingusage}"

		hm."OperatingState_Day${hm.currentDay}_cooling" = Op_coolingusage
		hm."OperatingState_Day${hm.currentDay}_heating" = Op_heatingusage
		hm."OperatingState_Day${hm.currentDay}_idle" = Op_idle
		hm."FanMode_Day${hm.currentDay}_On" = fan_on
		hm."FanMode_Day${hm.currentDay}_auto" = fan_auto

		hm.currentDay = dayNum
		hm.OperatingState_DayWeekago_cooling = hm."OperatingState_Day${hm.currentDay}_cooling"
		hm.OperatingState_DayWeekago_heating = hm."OperatingState_Day${hm.currentDay}_heating"
		hm.OperatingState_DayWeekago_idle = hm."OperatingState_Day${hm.currentDay}_idle"
		hm.FanMode_DayWeekago_On = hm."FanMode_Day${hm.currentDay}_On"
		hm.FanMode_DayWeekago_auto = hm."FanMode_Day${hm.currentDay}_auto"
		hm."OperatingState_Day${hm.currentDay}_cooling" = 0
		hm."OperatingState_Day${hm.currentDay}_heating" = 0
		hm."OperatingState_Day${hm.currentDay}_idle" = 0
		hm."FanMode_Day${hm.currentDay}_On" = 0
		hm."FanMode_Day${hm.currentDay}_auto" = 0

		def t1 = hm["OperatingState_Month${hm.currentMonth}_cooling"]?.toInteger() ?: 0
		hm."OperatingState_Month${hm.currentMonth}_cooling" = t1 + Op_coolingusage
		t1 = hm["OperatingState_Month${hm.currentMonth}_heating"]?.toInteger() ?: 0
		hm."OperatingState_Month${hm.currentMonth}_heating" = t1 + Op_heatingusage
		t1 = hm["OperatingState_Month${hm.currentMonth}_idle"]?.toInteger() ?: 0
		hm."OperatingState_Month${hm.currentMonth}_idle" = t1 + Op_idle
		t1 = hm["FanMode_Month${hm.currentMonth}_On"]?.toInteger() ?: 0
		hm."FanMode_Month${hm.currentMonth}_On" = t1 + fan_on
		t1 = hm["FanMode_Month${hm.currentMonth}_auto"]?.toInteger() ?: 0
		hm."FanMode_Month${hm.currentMonth}_auto" = t1 + fan_auto

		if(monthChange) {
			hm.currentMonth = monthNum
			hm.OperatingState_MonthYearago_cooling = hm."OperatingState_Month${hm.currentMonth}_cooling"
			hm.OperatingState_MonthYearago_heating = hm."OperatingState_Month${hm.currentMonth}_heating"
			hm.OperatingState_MonthYearago_idle = hm."OperatingState_Month${hm.currentMonth}_idle"
			hm.FanMode_MonthYearago_On = hm."FanMode_Month${hm.currentMonth}_On"
			hm.FanMode_MonthYearago_auto = hm."FanMode_Month${hm.currentMonth}_auto"
			hm."OperatingState_Month${hm.currentMonth}_cooling" = 0
			hm."OperatingState_Month${hm.currentMonth}_heating" = 0
			hm."OperatingState_Month${hm.currentMonth}_idle" = 0
			hm."FanMode_Month${hm.currentMonth}_On" = 0
			hm."FanMode_Month${hm.currentMonth}_auto" = 0
		}

		t1 = hm[OperatingState_thisYear_cooling]?.toInteger() ?: 0
		hm.OperatingState_thisYear_cooling = t1 + Op_coolingusage
		t1 = hm[OperatingState_thisYear_heating]?.toInteger() ?: 0
		hm.OperatingState_thisYear_heating = t1 + Op_heatingusage
		t1 = hm[OperatingState_thisYear_idle]?.toInteger() ?: 0
		hm.OperatingState_thisYear_idle = t1 + Op_idle
		t1 = hm[FanMode_thisYear_On]?.toInteger() ?: 0
		hm.FanMode_thisYear_On = t1 + fan_on
		t1 = hm[FanMode_thisYear_auto]?.toInteger() ?: 0
		hm.FanMode_thisYear_auto = t1 + fan_auto

		if(yearChange) {
			hm.currentYear = yearNum
			hm.OperatingState_lastYear_cooling = hm.OperatingState_thisYear_cooling
			hm.OperatingState_lastYear_heating = hm.OperatingState_thisYear_heating
			hm.OperatingState_lastYear_idle = hm.OperatingState_thisYear_idle
			hm.FanMode_lastYear_On = hm.FanMode_thisYear_On
			hm.FanMode_lastYear_auto = hm.FanMode_thisYear_auto

			hm.OperatingState_thisYear_cooling = 0
			hm.OperatingState_thisYear_heating = 0
			hm.OperatingState_thisYear_idle = O
			hm.FanMode_thisYear_On = 0
			hm.FanMode_thisYear_auto = 0
		}
	}
	state.historyStoreMap = hm
}

def getSumUsage(table, String strtyp) {
	//log.trace "getSumUsage...$strtyp Table size: ${table?.size()}"
	def totseconds = 0L
	def newseconds = 0L

	def hr
	def mins
	def myval
	def lasthr = 0
	def lastmins = 0
	def counting = false
	def firsttime = true
	def strthr
	def strtmin
	table.sort { a, b ->
		a[0] as Integer  <=> b[0] as Integer ?: a[1] as Integer <=> b[1] as Integer ?: a[2] <=> b[2]
	}
	//log.trace "$table"
	table.each() {
		hr = it[0].toInteger()
		mins = it[1].toInteger()
		myval = it[2].toString()
		//log.debug "${it[0]} ${it[1]} ${it[2]}"
		if(myval == strtyp) {
			if(!counting) {
				strthr = firstime ? lasthr : hr
				strtmin = firsttime ? lastmins : mins
				counting = true
			}
		} else if(counting) {
			newseconds = ((hr * 60 + mins) - (strthr * 60 + strtmin)) * 60
			totseconds += newseconds
			counting = false
			//log.debug "found $strtyp   starthr: $strthr  startmin: $strtmin  newseconds: $newseconds   totalseconds: $totseconds"
		}
		firsttime = false
	}
	if(counting) {
		def newDate = new Date()
		lasthr = newDate.format("H", location.timeZone).toInteger()
		lastmins = newDate.format("m", location.timeZone).toInteger()
		if( (hr*60+mins > lasthr*60+lastmins) ) {
			lasthr = 24
			lastmins = 0
		}
		newseconds = ((lasthr * 60 + lastmins) - (strthr * 60 + strtmin)) * 60
		totseconds += newseconds
		//log.debug "still counting found $strtyp  lasthr: $lasthr   lastmins: $lastmins  starthr: $strthr  startmin: $strtmin  newseconds: $newseconds   totalseconds: $totseconds"
	}
	//log.info "$strtyp totseconds: $totseconds"

	return totseconds.toInteger()
}

def initHistoryStore() {
	log.trace "initHistoryStore()..."

	def historyStoreMap = [:]
	def today = new Date()
	def dayNum = today.format("u", location.timeZone) as Integer // 1 = Monday,... 7 = Sunday
	def monthNum = today.format("MM", location.timeZone) as Integer
	def yearNum = today.format("YYYY", location.timeZone) as Integer

	//dayNum = 6   // TODO DEBUGGING

	historyStoreMap = [
		currentDay: dayNum,
		currentMonth: monthNum,
		currentYear: yearNum,
		OperatingState_DayWeekago_cooling: 0L, OperatingState_DayWeekago_heating: 0L, OperatingState_DayWeekago_idle: 0L,
		OperatingState_MonthYearago_cooling: 0L, OperatingState_MonthYearago_heating: 0L, OperatingState_MonthYearago_idle: 0L,
		OperatingState_thisYear_cooling: 0L, OperatingState_thisYear_heating: 0L, OperatingState_thisYear_idle: 0L,
		OperatingState_lastYear_cooling: 0L, OperatingState_lastYear_heating: 0L, OperatingState_lastYear_idle: 0L,
		FanMode_DayWeekago_On: 0L, FanMode_DayWeekago_auto: 0L,
		FanMode_MonthYearago_On: 0L, FanMode_MonthYearago_auto: 0L,
		FanMode_thisYear_On: 0L, FanMode_thisYear_auto: 0L,
		FanMode_lastYear_On: 0L, FanMode_lastYear_auto: 0L
	]

	for(int i = 1; i <= 7; i++) {
		historyStoreMap << ["OperatingState_Day${i}_cooling": 0L, "OperatingState_Day${i}_heating": 0L, "OperatingState_Day${i}_idle": 0L]
		historyStoreMap << ["FanMode_Day${i}_On": 0L, "FanMode_Day${i}_auto": 0L]
	}

	for(int i = 1; i <= 12; i++) {
		historyStoreMap << ["OperatingState_Month${i}_cooling": 0L, "OperatingState_Month${i}_heating": 0L, "OperatingState_Month${i}_idle": 0L]
		historyStoreMap << ["FanMode_Month${i}_On": 0L, "FanMode_Month${i}_auto": 0L]
	}

	//log.debug "historyStoreMap: $historyStoreMap"
	state.historyStoreMap = historyStoreMap
}

def getTodaysUsage() {
	def hm = getHistoryStore()
	def timeMap = [:]
	timeMap << ["cooling":["tData":secToTimeMap(hm?."OperatingState_Day${hm?.currentDay}_cooling"), "tSec":hm?."OperatingState_Day${hm?.currentDay}_cooling"]]
	timeMap << ["heating":["tData":secToTimeMap(hm?."OperatingState_Day${hm?.currentDay}_heating"), "tSec":hm?."OperatingState_Day${hm?.currentDay}_heating"]]
	timeMap << ["idle":["tData":secToTimeMap(hm?."OperatingState_Day${hm?.currentDay}_idle"), "tSec":hm?."OperatingState_Day${hm?.currentDay}_idle"]]
	timeMap << ["fanOn":["tData":secToTimeMap(hm?."FanMode_Day${hm?.currentDay}_On"), "tSec":hm?."FanMode_Day${hm?.currentDay}_on"]]
	timeMap << ["fanAuto":["tData":secToTimeMap(hm?."FanMode_Day${hm?.currentDay}_auto"), "tSec":hm?."FanMode_Day${hm?.currentDay}_auto"]]
	return timeMap
}

def getWeeksUsage() {
	def hm = getHistoryStore()
	def timeMap = [:]
	def coolVal = 0
	def heatVal = 0
	def idleVal = 0
	def fanOnVal = 0
	def fanAutoVal = 0
	for(int i = 1; i <= 7; i++) {
		coolVal = coolVal + hm?."OperatingState_Day${i}_cooling"?.toInteger()
		heatVal = heatVal + hm?."OperatingState_Day${i}_heating"?.toInteger()
		idleVal = idleVal + hm?."OperatingState_Day${i}_idle"?.toInteger()
		fanOnVal = fanOnVal + hm?."FanMode_Day${i}_On"?.toInteger()
		fanAutoVal = fanAutoVal + hm?."FanMode_Day${i}_auto"?.toInteger()
	}
	timeMap << ["cooling":["tData":secToTimeMap(coolVal), "tSec":coolVal]]
	timeMap << ["heating":["tData":secToTimeMap(heatVal), "tSec":heatVal]]
	timeMap << ["idle":["tData":secToTimeMap(idleVal), "tSec":idleVal]]
	timeMap << ["fanOn":["tData":secToTimeMap(fanOnVal), "tSec":fanOnVal]]
	timeMap << ["fanAuto":["tData":secToTimeMap(fanAutoVal), "tSec":fanAutoVal]]
	//log.debug "weeksUsage: ${timeMap}"
	return timeMap
}

def getMonthsUsage(monNum) {
	def hm = getHistoryStore()
	def timeMap = [:]
	def mVal = monNum ?: hm?.currentMonth
	timeMap << ["cooling":["tData":secToTimeMap(hm?."OperatingState_Month${mVal}_cooling"), "tSec":hm?."OperatingState_Month${mVal}_cooling"]]
	timeMap << ["heating":["tData":secToTimeMap(hm?."OperatingState_Month${mVal}_heating"), "tSec":hm?."OperatingState_Month${mVal}_heating"]]
	timeMap << ["idle":["tData":secToTimeMap(hm?."OperatingState_Month${mVal}_idle"), "tSec":hm?."OperatingState_Month${mVal}_idle"]]
	timeMap << ["fanOn":["tData":secToTimeMap(hm?."FanMode_Month${mVal}_On"), "tSec":hm?."FanMode_Month${mVal}_on"]]
	timeMap << ["fanAuto":["tData":secToTimeMap(hm?."FanMode_Month${mVal}_auto"), "tSec":hm?."FanMode_Month${mVal}_auto"]]
	//log.debug "monthsUsage: ${timeMap}"
	return timeMap
}

def getYearsUsage() {
	def hm = getHistoryStore()
	def timeMap = [:]
	def coolVal = 0
	def heatVal = 0
	def idleVal = 0
	def fanOnVal = 0
	def fanAutoVal = 0
	for(int i = 1; i <= 12; i++) {
		coolVal = coolVal + hm?."OperatingState_Month${i}_cooling"?.toInteger()
		heatVal = heatVal + hm?."OperatingState_Month${i}_heating"?.toInteger()
		idleVal = idleVal + hm?."OperatingState_Month${i}_idle"?.toInteger()
		fanOnVal = fanOnVal + hm?."FanMode_Month${i}_On"?.toInteger()
		fanAutoVal = fanAutoVal + hm?."FanMode_Month${i}_auto"?.toInteger()
	}
	timeMap << ["cooling":["tData":secToTimeMap(coolVal), "tSec":coolVal]]
	timeMap << ["heating":["tData":secToTimeMap(heatVal), "tSec":heatVal]]
	timeMap << ["idle":["tData":secToTimeMap(idleVal), "tSec":idleVal]]
	timeMap << ["fanOn":["tData":secToTimeMap(fanOnVal), "tSec":fanOnVal]]
	timeMap << ["fanAuto":["tData":secToTimeMap(fanAutoVal), "tSec":fanAutoVal]]
	//log.debug "yearsUsage: ${timeMap}"
	return timeMap
}

def doSomething() {
	getNestMgrReport()
	//getTodaysUsage()
	//getWeeksUsage()
	//getMonthsUsage()
	//getYearsUsage()
}

def getHistoryStore() {
	//log.trace "getHistoryStore()..."
	def hm = state?.historyStoreMap
	if(hm == null) {
		log.error "hm is null"
		return
	}
	def Op_coolingusage = getSumUsage(state.operatingStateTable, "cooling").toInteger()
	def Op_heatingusage = getSumUsage(state.operatingStateTable, "heating").toInteger()
	def Op_idle = getSumUsage(state.operatingStateTable, "idle").toInteger()
	def fan_on = getSumUsage(state.fanModeTable, "on").toInteger()
	def fan_auto = getSumUsage(state.fanModeTable, "auto").toInteger()

	//log.info "fanon ${fan_on}  fanauto: ${fan_auto} opidle: ${Op_idle}  cool: ${Op_coolingusage} heat: ${Op_heatingusage}"
	//log.debug "currentDay ${hm.currentDay} | currentMonth ${hm.currentMonth}  | currentYear: ${hm.currentYear}"

	hm."OperatingState_Day${hm.currentDay}_cooling" = Op_coolingusage
	hm."OperatingState_Day${hm.currentDay}_heating" = Op_heatingusage
	hm."OperatingState_Day${hm.currentDay}_idle" = Op_idle
	hm."FanMode_Day${hm.currentDay}_On" = fan_on
	hm."FanMode_Day${hm.currentDay}_auto" = fan_auto

	def t1 = hm["OperatingState_Month${hm.currentMonth}_cooling"]?.toInteger() ?: 0
	hm."OperatingState_Month${hm.currentMonth}_cooling" = t1 + Op_coolingusage
	t1 = hm["OperatingState_Month${hm.currentMonth}_heating"]?.toInteger() ?: 0
	hm."OperatingState_Month${hm.currentMonth}_heating" = t1 + Op_heatingusage
	t1 = hm["OperatingState_Month${hm.currentMonth}_idle"]?.toInteger() ?: 0
	hm."OperatingState_Month${hm.currentMonth}_idle" = t1 + Op_idle
	t1 = hm["FanMode_Month${hm.currentMonth}_On"]?.toInteger() ?: 0
	hm."FanMode_Month${hm.currentMonth}_On" = t1 + fan_on
	t1 = hm["FanMode_Month${hm.currentMonth}_auto"]?.toInteger() ?: 0
	hm."FanMode_Month${hm.currentMonth}_auto" = t1 + fan_auto

	t1 = hm[OperatingState_thisYear_cooling]?.toInteger() ?: 0
	hm.OperatingState_thisYear_cooling = t1 + Op_coolingusage
	t1 = hm[OperatingState_thisYear_heating]?.toInteger() ?: 0
	hm.OperatingState_thisYear_heating = t1 + Op_heatingusage
	t1 = hm[OperatingState_thisYear_idle]?.toInteger() ?: 0
	hm.OperatingState_thisYear_idle = t1 + Op_idle
	t1 = hm[FanMode_thisYear_On]?.toInteger() ?: 0
	hm.FanMode_thisYear_On = t1 + fan_on
	t1 = hm[FanMode_thisYear_auto]?.toInteger() ?: 0
	hm.FanMode_thisYear_auto = t1 + fan_auto

	return hm
}

def addNewData() {
	def currentTemperature = getTemp()
	def currentcoolSetPoint = getCoolTemp()
	def currentheatSetPoint = getHeatTemp()
	def currentoperatingState = getHvacState()
	def currenthumidity = getHumidity()
	def currentfanMode = getFanMode()
	def currentExternal = state?.curExtTemp

	def temperatureTable = state?.temperatureTable
	def operatingStateTable = state?.operatingStateTable
	def humidityTable = state?.humidityTable
	def coolSetpointTable = state?.coolSetpointTable
	def heatSetpointTable = state?.heatSetpointTable
	def extTempTable = state?.extTempTable
	def fanModeTable = state?.fanModeTable

	// add latest coolSetpoint & temperature readings for the graph
	def newDate = new Date()
	def hr = newDate.format("H", location.timeZone) as Integer
	def mins = newDate.format("m", location.timeZone) as Integer

	state.temperatureTable = addValue(temperatureTable, hr, mins, currentTemperature)
	state.operatingStateTable = addValue(operatingStateTable, hr, mins, currentoperatingState)
	state.humidityTable = addValue(humidityTable, hr, mins, currenthumidity)
	state.coolSetpointTable = addValue(coolSetpointTable, hr, mins, currentcoolSetPoint)
	state.heatSetpointTable = addValue(heatSetpointTable, hr, mins, currentheatSetPoint)
	state.extTempTable = addValue(extTempTable, hr, mins, currentExternal)
	state.fanModeTable = addValue(fanModeTable, hr, mins, currentfanMode)
}

def addValue(table, hr, mins, val) {
	def newTable = table
        if(table?.size() > 2) {
                def last = table?.last()[2]
                def secondtolast = table[-2][2]
                if(val == last && val == secondtolast) {
                        newTable = table?.take(table.size() - 1)
                }
        }
        newTable?.add([hr, mins, val])
        return newTable
}

def getIntListAvg(itemList) {
	//log.debug "itemList: ${itemList}"
	def avgRes = 0
	def iCnt = itemList?.size()
	if(iCnt >= 1) {
		if(iCnt > 1) {
			avgRes = (itemList?.sum().toDouble() / iCnt.toDouble()).round(0)
		} else { itemList?.each { avgRes = avgRes + it.toInteger() } }
	}
	//log.debug "[getIntListAvg] avgRes: $avgRes"
	return avgRes.toInteger()
}

def secToTimeMap(long seconds) {
	long sec = seconds % 60
	long minutes = seconds % 3600 / 60
	long hours = seconds % 86400 / 3600
	long days = seconds / 86400
	long years = days / 365
	def res = ["m":minutes, "h":hours, "d":days, "y":years]
	return res
}

def getStartTime() {
	def startTime = 24
	if (state?.temperatureTable?.size()) { startTime = state?.temperatureTable?.min{it[0].toInteger()}[0].toInteger() }
	if (state?.temperatureTableYesterday?.size()) { startTime = Math.min(startTime, state?.temperatureTableYesterday?.min{it[0].toInteger()}[0].toInteger()) }
	//LogAction("startTime ${startTime}", "trace")
	return startTime
}

def getMinTemp() {
	def has_weather = false
	if(state?.curExtTemp != null) { has_weather = true }

	def list = []
	if (state?.temperatureTableYesterday?.size() > 0) { list.add(state?.temperatureTableYesterday?.min { it[2] }[2].toInteger()) }
	if (state?.temperatureTable?.size() > 0) { list.add(state?.temperatureTable.min { it[2] }[2].toInteger()) }
	//if (state?.can_cool && state?.coolSetpointTable?.size() > 0) { list.add(state?.coolSetpointTable.min { it[2] }[2].toInteger()) }
	//if (state?.can_heat && state?.heatSetpointTable?.size() > 0) { list.add(state?.heatSetpointTable.min { it[2] }[2].toInteger()) }
	if (has_weather && state?.extTempTable?.size() > 0) { list.add(state?.extTempTable.min { it[2] }[2].toInteger()) }
	//LogAction("getMinTemp: ${list.min()} result: ${list}", "trace")
	return list?.min()
}

def getMaxTemp() {
	def has_weather = false
	if(state?.curExtTemp != null) { has_weather = true }

	def list = []
	if (state?.temperatureTableYesterday?.size() > 0) { list.add(state?.temperatureTableYesterday.max { it[2] }[2].toInteger()) }
	if (state?.temperatureTable?.size() > 0) { list.add(state?.temperatureTable.max { it[2] }[2].toInteger()) }
	//if (state?.can_cool && state?.coolSetpointTable?.size() > 0) { list.add(state?.coolSetpointTable.max { it[2] }[2].toInteger()) }
	//if (state?.can_heat && state?.heatSetpointTable?.size() > 0) { list.add(state?.heatSetpointTable.max { it[2] }[2].toInteger()) }
	if (has_weather && state?.extTempTable?.size() > 0) { list.add(state?.extTempTable.max { it[2] }[2].toInteger()) }
	//LogAction("getMaxTemp: ${list.max()} result: ${list}", "trace")
	return list?.max()
}

def getGraphHTML() {
	try {
		//LogAction("State Size: ${getStateSize()} (${getStateSizePerc()}%)")
		def leafImg = state?.hasLeaf ? getImgBase64(getImg("nest_leaf_on.gif"), "gif") : getImgBase64(getImg("nest_leaf_off.gif"), "gif")
		def updateAvail = !state.updateAvailable ? "" : "<h3>Device Update Available!</h3>"
		def clientBl = state?.clientBl ? """<h3>Your Manager client has been blacklisted!\nPlease contact the Nest Manager developer to get the issue resolved!!!</h3>""" : ""
		def timeToTarget = device.currentState("timeToTarget").stringValue
		def chartHtml = (
				state.temperatureTable?.size() > 0 &&
				state.operatingStateTable?.size() > 0 &&
				state.temperatureTableYesterday?.size() > 0 &&
				state.humidityTable?.size() > 0 &&
				state.coolSetpointTable?.size() > 0 &&
				state.heatSetpointTable?.size() > 0) ? showChartHtml() : hideChartHtml()

		def html = """
		<!DOCTYPE html>
		<html>
			<head>
				<meta http-equiv="cache-control" content="max-age=0"/>
				<meta http-equiv="cache-control" content="no-cache"/>
				<meta http-equiv="expires" content="0"/>
				<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
				<meta http-equiv="pragma" content="no-cache"/>
				<meta name="viewport" content="width = device-width, user-scalable=no, initial-scale=1.0">
				<link rel="stylesheet prefetch" href="${getCssData()}"/>
				<script type="text/javascript" src="${getChartJsData()}"></script>
			</head>
			<body>
				${clientBl}
				${updateAvail}

				${chartHtml}

				<br></br>
				<table
				<thead>
				  <th>Time to Target</th>
				</thead>
				<tbody>
				  <tr>
					<td>${timeToTarget}</td>
				  </tr>
				</tbody>
				</table>
				<table>
				<col width="40%">
				<col width="20%">
				<col width="40%">
				<thead>
				  <th>Network Status</th>
				  <th>Leaf</th>
				  <th>API Status</th>
				</thead>
				<tbody>
				  <tr>
					<td>${state?.onlineStatus.toString()}</td>
					<td><img src="${leafImg}" class="leafImg"></img></td>
					<td>${state?.apiStatus}</td>
				  </tr>
				</tbody>
			  </table>

			  <p class="centerText">
				<a href="#openModal" class="button">More info</a>
			  </p>

			  <div id="openModal" class="topModal">
				<div>
				  <a href="#close" title="Close" class="close">X</a>
				  <table>
				  	<tbody>
					  <tr>
					    <th>Firmware Version</th>
					    <th>Debug</th>
					    <th>Device Type</th>
					  </tr>
					  <td>${state?.softwareVer.toString()}</td>
					  <td>${state?.debugStatus}</td>
					  <td>${state?.devTypeVer.toString()}</td>
					</tbody>
				  </table>
				  <table>
					<thead>
					  <th>Nest Checked-In</th>
					  <th>Data Last Received</th>
					</thead>
					<tbody>
					  <tr>
						<td class="dateTimeText">${state?.lastConnection.toString()}</td>
						<td class="dateTimeText">${state?.lastUpdatedDt.toString()}</td>
					  </tr>
				  </table>
				</div>
			  </div>
			</body>
		</html>
		"""
		render contentType: "text/html", data: html, status: 200
	} catch (ex) {
		log.error "graphHTML Exception:", ex
		exceptionDataHandler(ex.message, "graphHTML")
	}
}

def showChartHtml() {
	def tempStr = "°F"
	if ( wantMetric() ) {
		tempStr = "°C"
	}

	def has_weather = false
	def commastr = ""
	if(state?.curExtTemp != null) { has_weather = true; commastr = "," }

	def coolstr1 = "data.addColumn('number', 'CoolSP');"
	def coolstr2 =  getDataString(5)
	def coolstr3 = "4: {targetAxisIndex: 1, type: 'line', color: '#85AAFF', lineWidth: 1},"

	def heatstr1 = "data.addColumn('number', 'HeatSP');"
	def heatstr2 = getDataString(6)
	def heatstr3 = "5: {targetAxisIndex: 1, type: 'line', color: '#FF4900', lineWidth: 1}${commastr}"

	def weathstr1 = "data.addColumn('number', 'ExtTmp');"
	def weathstr2 = getDataString(7)
	def weathstr3 = "6: {targetAxisIndex: 1, type: 'line', color: '#000000', lineWidth: 1}"

	if (state?.can_cool && !state?.can_heat) { coolstr3 = "4: {targetAxisIndex: 1, type: 'line', color: '#85AAFF', lineWidth: 1}${commastr}" }

	if (!state?.can_cool && state?.can_heat) { heatstr3 = "4: {targetAxisIndex: 1, type: 'line', color: '#FF4900', lineWidth: 1}${commastr}" }

	if (!state?.can_cool) {
		coolstr1 = ""
		coolstr2 = ""
		coolstr3 = ""
		weathstr3 = "5: {targetAxisIndex: 1, type: 'line', color: '#000000', lineWidth: 1}"
	}

	if (!state?.can_heat) {
		heatstr1 = ""
		heatstr2 = ""
		heatstr3 = ""
		weathstr3 = "5: {targetAxisIndex: 1, type: 'line', color: '#000000', lineWidth: 1}"
	}

	if (!has_weather) {
		weathstr1 = ""
		weathstr2 = ""
		weathstr3 = ""
	}

	//LogAction("has_weather: ${has_weather},  weathstr1: ${weathstr1}  weathstr3: ${weathstr3}")

	def minval = getMinTemp()
	def minstr = "minValue: ${minval},"

	def maxval = getMaxTemp()
	def maxstr = "maxValue: ${maxval},"

	def differ = maxval - minval
	//if (differ > (maxval/4) || differ < (wantMetric() ? 7:15) ) {
		minstr = "minValue: ${(minval - (wantMetric() ? 2:5))},"
		//if (differ < (wantMetric() ? 7:15) ) {
			maxstr = "maxValue: ${(maxval + (wantMetric() ? 2:5))},"
		//}
	//}

	def data = """
	<script type="text/javascript">
		google.charts.load('current', {packages: ['corechart']});
		google.charts.setOnLoadCallback(drawGraph);
		function drawGraph() {
			var data = new google.visualization.DataTable();
			data.addColumn('timeofday', 'time');
			data.addColumn('number', 'Temp (Y)');
			data.addColumn('number', 'Temp (T)');
			data.addColumn('number', 'Operating');
			data.addColumn('number', 'Humidity');
			${coolstr1}
			${heatstr1}
			${weathstr1}
			data.addRows([
				${getDataString(1)}
				${getDataString(2)}
				${getDataString(3)}
				${getDataString(4)}
				${coolstr2}
				${heatstr2}
				${weathstr2}
			]);
			var options = {
				width: '100%',
				height: '100%',
				animation: {
					duration: 1500,
					startup: true
				},
				hAxis: {
					format: 'H:mm',
					minValue: [${getStartTime()},0,0],
					slantedText: true,
					slantedTextAngle: 30
				},
				series: {
					0: {targetAxisIndex: 1, type: 'area', color: '#FFC2C2', lineWidth: 1},
					1: {targetAxisIndex: 1, type: 'area', color: '#FF0000'},
					2: {targetAxisIndex: 0, type: 'area', color: '#ffdc89'},
					3: {targetAxisIndex: 0, type: 'area', color: '#B8B8B8'},
					${coolstr3}
					${heatstr3}
					${weathstr3}
				},
				vAxes: {
					0: {
						title: 'Humidity (%)',
						format: 'decimal',
						minValue: 0,
						maxValue: 100,
						textStyle: {color: '#B8B8B8'},
						titleTextStyle: {color: '#B8B8B8'}
					},
					1: {
						title: 'Temperature (${tempStr})',
						format: 'decimal',
						${minstr}
						${maxstr}
						textStyle: {color: '#FF0000'},
						titleTextStyle: {color: '#FF0000'}
					}
				},
				legend: {
					position: 'bottom',
					maxLines: 4,
					textStyle: {color: '#000000'}
				},
				chartArea: {
					left: '12%',
					right: '18%',
					top: '3%',
					bottom: '20%',
					height: '85%',
					width: '100%'
				}
			};
			var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
			chart.draw(data, options);
		}
	  </script>
	  <h4 style="font-size: 22px; font-weight: bold; text-align: center; background: #00a1db; color: #f5f5f5;">Event History</h4>
	  <div id="chart_div" style="width: 100%; height: 225px;"></div>
	"""
	return data
}

def hideChartHtml() {
	def data = """
	<h4 style="font-size: 22px; font-weight: bold; text-align: center; background: #00a1db; color: #f5f5f5;">Event History</h4>
	<br></br>
	<div class="centerText">
	  <p>Waiting for more data to be collected...</p>
	  <p>This may take at least 24 hours</p>
	</div>
	"""
	return data
}

void updateNestReportData() {
	nestReportStatusEvent()
}

def cleanDevLabel() {
	return device.label.toString().replaceAll("-", "")
}

def getDayTimePerc(val,data) {
	//log.debug "getDayTimePerc($val, $data)"
	//log.debug "getDayElapSec: ${getDayElapSec()}"
	if(!data) { return null }
	return (int) ((val.toInteger()/getDayElapSec())*100).toDouble().round(0)
}

def getDayElapSec() {
	Calendar c = Calendar.getInstance();
	long now = c.getTimeInMillis();
	c.set(Calendar.HOUR_OF_DAY, 0);
	c.set(Calendar.MINUTE, 0);
	c.set(Calendar.SECOND, 0);
	c.set(Calendar.MILLISECOND, 0);
	long passed = now - c.getTimeInMillis();
	return (long) passed / 1000;
}

def getTimeMapString(data) {
	if(!data) { return null }
	def str = ""
	def d = data?.d
	def h = data?.h
	def m = data?.m
	if(h>0 || m>0 || d>0) {
		if(d>0) {
			str += "$d day${d>1 ? "s" : ""}"
			str += d>0 || m>0 ? " and " : ""
		}
		if (h>0) {
			str += h>0 ? "$h Hour${h>1 ? "s" : ""} " : ""
			str += m>0 ? "and " : ""
		}
		if (m>0) {
			str += m>0 ? "$m minute${m>1 ? "s" : ""}" : ""
		}
		return str
	} else {
		return null
	}
}

private def textDevName()  	{ return "Nest ${virtDevName()}Thermostat${appDevName()}" }
private def appDevType()   	{ return false }
private def appDevName()   	{ return appDevType() ? " (Dev)" : "" }
private def virtType()		{ return false }
private def virtDevName()  	{ return virtType() ? "Virtual " : "" }


def getNestMgrReport() {
	//log.trace "getNestMgrReport()..."
	def str = ""
	if(state?.voiceReportPrefs?.allowVoiceZoneRprt || state?.voiceReportPrefs?.allowVoiceUsageRprt) {
		str += "Here is the up to date ${cleanDevLabel()} Report. "

		if(state?.voiceReportPrefs?.vRprtSched == true) {
			if(state?.voiceReportPrefs?.allowVoiceZoneRprt == false) {
				Logger("getNestMgrReport: Zone status voice reports have been disabled by Nest manager app preferences", "info")
				str += " automation schedule voice reports have been disabled by Nest manager app preferences. Please open your manager app and change the preferences and try again. "
			}
			else {
				def schRprtDesc = parent?.reqSchedInfoRprt(this)
				if(schRprtDesc) {
					str += schRprtDesc.toString() + "  "
					str += " Now let's move on to usage.  "
				}
			}
		}

		if(state?.voiceReportPrefs?.vRprtUsage == true) {
			if(state?.voiceReportPrefs?.allowVoiceUsageRprt == false) {
				Logger("getNestMgrReport: Zone status voice reports have been disabled by Nest manager app preferences", "info")
				str += "Zone status voice reports have been disabled by Nest manager app preferences. Please open your manager app and change the preferences and try again. "
			} else {
				str += getUsageVoiceReport("runtimeToday")
			}
		}
	} else {
		str += "All voice reports have been disabled by Nest Manager app preferences. Please open your manager app and change the preferences and try again. "
		return str
	}
	log.trace "NestMgrReport Response: ${str}"
	incVoiceRprtCnt()
	return str
}

def incVoiceRprtCnt() {
	def rCnt = state?.voiceRprtCnt ?: 0
	rCnt = rCnt?.toInteger()+1
	//Logger("Voice Report Count: $rCnt", "info")
	state?.voiceRprtCnt = rCnt?.toInteger()
}

def voiceRprtCnt() { return state?.voiceRprtCnt ?: 0 }

def getUsageVoiceReport(type) {
	switch(type) {
		case "runtimeToday":
			return generateUsageText("today" ,getTodaysUsage())
			break
		case "runtimeWeek":
			return generateUsageText("week" ,getWeeksUsage())
			break
		case "runtimeMonth":
			return generateUsageText("month" ,getMonthsUsage())
 			break
		default:
			return "I'm sorry but the report type received was not valid"
			break
	}
}

def generateUsageText(timeType, timeMap) {
	def str = ""
	if(timeType && timeMap) {
		def hData = null; def cData = null;	def iData = null; def f1Data = null; def f0Data = null;

		timeMap?.each { item ->
			def type = item?.key
			def tData = item?.value?.tData
			def h = tData?.h.toInteger()
			def m = tData?.m.toInteger()
			def d = tData?.d.toInteger()
			def y = tData?.y.toInteger()
			if(h>0 || m>0 || d>0) {
				if(type == "heating") 	{ hData = item }
				if(type == "cooling") 	{ cData = item }
				if(type == "idle")	  	{ iData = item }
				//if(type == "fanOn")   	{ f1Data = item }
				//if(type == "fanAuto")	{ f0Data = item }
			}
		}
		if(hData || cData || iData) {// || f1Data || f0Data) {
			str += " Based on the devices activity so far today. "
			def showAnd = hData || cData //|| f0Data || f1Data
			def iTime = 0; def hTime = 0; def cTime = 0;
			def iTmStr; def hTmStr; def cTmStr;

			//Fills Idle Usage Data
			if(iData?.key == "idle") {
				iTmStr = getTimeMapString(iData?.value?.tData)
				iTime = getDayTimePerc(iData?.value?.tSec.toInteger(),iData?.value?.tData)
			}
			//Fills Heating Usage Data
			if(hData?.key == "heating") {
				hTmStr = getTimeMapString(hData?.value?.tData)
				hTime = getDayTimePerc(hData?.value?.tSec.toInteger(),hData?.value?.tData)
			}

			//Fills Cooling Usage Data
			if(cData?.key == "cooling") {
				cTmStr = getTimeMapString(cData?.value?.tData)
				cTime = getDayTimePerc(cData?.value?.tSec.toInteger(),cData?.value?.tData)
			}

			def tmMap = new TreeMap<Integer, String>(["${hTime}":"heating", "${cTime}":"cooling", "${iTime}":"idle"])
			def mSz = tmMap?.size()
			def last = null
			tmMap?.reverseEach {
				if(it?.key.toInteger() > 0) {
					switch(it?.value.toString()) {
						case "idle":
							def lastOk = (last in ["cooling", "heating"])
							str += lastOk ? " and" : ""
							str += getIdleUsageDesc(iTime,, iTmStr, timeType)
							last = it?.value.toString()
							break
						case "heating":
							def lastOk = (last in ["idle", "cooling"])
							str += lastOk ? " and" : ""
							str += getHeatUsageDesc(hTime, hTmStr, timeType)
							last = it?.value.toString()
							break
						case "cooling":
							def lastOk = (last in ["idle", "heating"])
							str += lastOk ? " and" : ""
							str += getCoolUsageDesc(cTime, cTmStr, timeType)
							last = it?.value.toString()
							break
					}
					str += ". "
				}
			}

			/*if(type in ["fanAuto", "fanOn"]) {
				//not sure how to format the fan strings yet

			}*/
			//log.debug "idle: $iTime%"
			//log.debug "heating: $hTime%"
			//log.debug "cooling: $cTime%"
		} else {
			str += " There doesn't appear to have been any usage data collected yet.  "
		}
	}
	str += " That is all for the current nest report. Check back later and have a wonderful day..."
	//log.debug "generateUsageText: $str"
	return str
}

def getIdleUsageDesc(perc, tmStr, timeType) {
	def str = ""
	if(timeType == "today") {
		if (perc>0 && perc <=100) {
			str += " The device has been idle ${perc} percent of the day at "
			str += tmStr
		}
	}
}

def getHeatUsageDesc(perc, tmStr, timeType) {
	def str = ""
	if(timeType == "today") {
		if (perc>=86) {
			str += " spent "
			str += tmStr
			str += " heating your home ${timeType != "today" ? "this " : ""}${timeType} "
		}
		else if(perc>=66 && perc<=85) {
			str += " it must have been freezing today because it was heating your home for "
			str += tmStr
		}
		else if (perc>=34 && perc<66) {
			str += " it's like the weather was a bit chilly today because your device spent "
			str += tmStr
			str += " trying to keep your home cozy "
		}
		else if (perc>0 && perc<34) {
			str += " only spent ${tmStr} heating up the home"
		}
	}
	return str
}

def getCoolUsageDesc(perc, tmStr, timeType) {
	def str = ""
	if(timeType == "today") {
		if(perc>=66 && perc<=100) {
			str += " it must have been scorching outside because is was cooling for "
			str += tmStr
		}
		else if (perc>=34 && perc<66) {
			str += " it must have been a beautiful day because your device only cooled for "
			str += tmStr
		}
		else if (perc>0 && perc<34) {
			str += " it must have been a beautiful day because your device only cooled for "
			str += tmStr
		}
	}
	return str
}
