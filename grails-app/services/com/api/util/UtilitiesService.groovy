package com.api.util

import java.text.MessageFormat
import grails.transaction.Transactional
import schools.exceptions.NotFoundException
import schools.exceptions.ConflictException
import schools.exceptions.BadRequestException
import java.security.MessageDigest
import schools.ValidAccess
import grails.converters.*
import com.api.cendi.School
import com.api.cendi.Phone
import java.security.MessageDigest

class UtilitiesService {

    public def verifyJSON(def jsonSchool) {
    	boolean validJson = false
    	if(jsonSchool.name && jsonSchool.street && jsonSchool.number && jsonSchool.neighborhood && jsonSchool.postal_code && jsonSchool.principal && jsonSchool.opening_date && jsonSchool.phones){
    		validJson = true
    	}
    	validJson 
    }

    public Map fillSchoolResult(def jsonSchool,def phone){
    	Map jsonResult = [:]

    	jsonResult.school_id = jsonSchool.school_id
		jsonResult.name = jsonSchool.name
		jsonResult.street = jsonSchool.street
		jsonResult.number = jsonSchool.number
		jsonResult.neighborhood = jsonSchool.neighborhood
		jsonResult.postal_code = jsonSchool.postal_code
		jsonResult.principal = jsonSchool.principal
		jsonResult.phones = phone

		jsonResult
    }

    public Map fillSchoolResult(School school){
    	Map jsonResult = [:]
    	jsonResult.school_id = school.school_id
		jsonResult.name = school.name
		jsonResult.street = school.street
		jsonResult.number = school.number
		jsonResult.neighborhood = school.neighborhood
		jsonResult.postal_code = school.postal_code
		jsonResult.principal = school.principal
		jsonResult.phones = school.phones

		jsonResult
    }

    public School fillSchoolObject(def jsonResult,School temp_school){
    	def school = new School()
    	//school.school_id = jsonResult.school_id
		school.school_id = MessageDigest.getInstance("MD5").digest(jsonResult.name.bytes).encodeHex().toString()
		school.name = jsonResult.name
		school.street = jsonResult.street
		school.number = jsonResult.number
		school.neighborhood = jsonResult.neighborhood
		school.postal_code = jsonResult.postal_code
		school.principal = jsonResult.principal
		school.phones = temp_school
		school
    }

    public void existSchool(def school,def school_id){
    	if(!school){
    		throw new NotFoundException("The school with the school_id = "+school_id+" not found")
    	}
    }

}
