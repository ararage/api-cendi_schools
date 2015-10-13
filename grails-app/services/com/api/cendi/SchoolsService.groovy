package com.api.cendi

import java.text.MessageFormat
import grails.transaction.Transactional
import schools.exceptions.NotFoundException
import schools.exceptions.ConflictException
import schools.exceptions.BadRequestException
import schools.ValidAccess
import com.api.cendi.School
import com.api.cendi.Phone
import com.api.util.UtilitiesService
import grails.converters.*
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class SchoolsService {

    def postSchool(def jsonSchool, def dominio){
        Map jsonResult = [:]
    	def responseMessage = ''
    	School school = new School()
        def validAccess
        println"QUE PASO"
    	String uIdSchool
        int uNumeroIdSchool = 0
        def findSchool

        try{
            uIdSchool = school.last()._id
        }catch(Exception e){}

        if(uIdSchool){
            uNumeroIdSchool = Integer.valueOf(uIdSchool)
        }
        uNumeroIdSchool++
    
        println "Numero "+uNumeroIdSchool

    	if(!jsonSchool){
    		 throw new ConflictException("Empty JSON!")
    	}
    	
        if(jsonSchool.opening_date){
             try{
                jsonResult.opening_date = ISODateTimeFormat.dateTimeParser().parseDateTime(jsonSchool?.opening_date).toDate()
            }catch(Exception e){
                throw new BadRequestException("Wrong date format in date_of_birth. Must be ISO json format")
            }
        }else{
            throw new ConflictException("Bad JSON: birth not found on the json!")
        }

        println "cheque las fechas"

    	if(!new UtilitiesService().verifyJSON(jsonSchool)){
    		throw new ConflictException("Invalid Json")
    	}
      
        if(!jsonSchool.phones){
            throw new ConflictException("Bad JSON: phones array not found on the json!")
        }

        findSchool = School.findByNameAndPrincipal(""+jsonSchool?.name,""+jsonSchool?.principal)

        if(new UtilitiesService().existSchoolCheck(findSchool)){
            throw new ConflictException("The School with the school name: "+jsonSchool?.name+" already exists.")
        }
        //validAccess = new ValidAccess()
        

        for(element in jsonSchool.phones){
        	def phoneClass = new Phone(
        		school_id : uNumeroIdSchool,
        		number : element.number
        	)

        	if(!phoneClass.validate()) {
                phoneClass.errors.allErrors.each {
                    responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
                }
                throw new BadRequestException(responseMessage)
            }
            school.phones.add(
                school_id:""+phoneClass.school_id,
                number:""+phoneClass.number
            )
        }


		/*jsonResult.school_id = jsonSchool.school_id
		jsonResult.name = jsonSchool.name
		jsonResult.street = jsonSchool.street
		jsonResult.number = jsonSchool.number
		jsonResult.neighborhood = jsonSchool.neighborhood
		jsonResult.postal_code = jsonSchool.postal_code
		jsonResult.principal = jsonSchool.principal
		jsonResult.phones = school.phones*/
		jsonResult = new UtilitiesService().fillSchoolResult(jsonSchool,school.phones,uNumeroIdSchool)
		
        println "Json Result "+jsonResult
        school = new UtilitiesService().fillSchoolObject(jsonResult,school)
		println "Le school "+school
        /*
		school.school_id = jsonResult.school_id
		school.name = jsonResult.name
		school.street = jsonResult.street
		school.number = jsonResult.number
		school.neighborhood = jsonResult.neighborhood
		school.postal_code = jsonResult.postal_code
		school.principal = jsonResult.principal
		*/

       	if(!school.validate()) {
            school.errors.allErrors.each {
                responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
            }
            throw new BadRequestException(responseMessage)
        }

        school.save(flush:true)
        jsonResult
	}

    def getShcool(def params, def dominio){
        Map jsonResult = [:]
        def schoolResult
        if(!params.school_id){
            throw new NotFoundException("Invalid URL, missing school_id")
        }

        schoolResult = School.findBySchool_id(params.school_id)
        
        new UtilitiesService().existSchool(userResult,params.school_id)

        jsonResult = new UtilitiesService().fillSchoolResult()
        jsonResult
    }
}
