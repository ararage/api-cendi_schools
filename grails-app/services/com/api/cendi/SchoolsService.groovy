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

		jsonResult = new UtilitiesService().fillSchoolResult(jsonSchool,school.phones,uNumeroIdSchool)
		
        school = new UtilitiesService().fillSchoolObject(jsonResult,school)
		
       	if(!school.validate()) {
            school.errors.allErrors.each {
                responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
            }
            throw new BadRequestException(responseMessage)
        }

        school.save(flush:true)
        jsonResult
	}

    def getSchool(def params, def dominio){
        Map jsonResult = [:]
        def schoolResult
        if(!params.school_id){
            throw new NotFoundException("Invalid URL, missing school_id")
        }

        schoolResult = School.findBySchool_id(params.school_id)
        
        new UtilitiesService().existSchool(schoolResult,params.school_id)

        jsonResult = new UtilitiesService().fillSchoolResult(schoolResult)
        jsonResult
    }

    def putSchool(def params,def jsonSchool,def dominio){
        Map jsonResult = [:]
        def newSchool

        if(!jsonSchool){
            throw new ConflictException("Empty JSON!")
        }

        newSchool = School.findBySchool_id(params.school_id)
        println "School "+newSchool
        if(!new UtilitiesService().existSchoolCheck(newSchool)){
            throw new ConflictException("The School with the school_id: "+params.school_id+" doesn't exists.")
        }

        if(jsonSchool?.name){
            newSchool.name = jsonSchool?.name
        }

        if(jsonSchool?.neighborhood){
            newSchool.neighborhood = jsonSchool?.neighborhood
        }

        if(jsonSchool?.street){
            newSchool.street = jsonSchool?.street
        }

        if(jsonSchool?.number){
            newSchool.number = jsonSchool?.number
        }

        if(jsonSchool?.postal_code){
            newSchool.postal_code = jsonSchool?.postal_code
        }

        if(jsonSchool?.principal){
            newSchool.principal = jsonSchool?.principal
        }

        if(jsonSchool?.opening_date){
            try{
                newSchool.opening_date = ISODateTimeFormat.dateTimeParser().parseDateTime(jsonSchool?.opening_date).toDate()
            }catch(Exception e){
                throw new BadRequestException("Wrong date format in date_of_birth. Must be ISO json format")
            }
        }

        if(jsonSchool?.phones){
            newSchool.phones = []
            for(element in jsonSchool.phones){
                def phoneClass = new Phone(
                    school_id : newSchool.school_id,
                    number : element.number
                )

                if(!phoneClass.validate()) {
                    phoneClass.errors.allErrors.each {
                        responseMessage += MessageFormat.format(it.defaultMessage, it.arguments) + " "
                    }
                    throw new BadRequestException(responseMessage)
                }
                newSchool.phones.add(
                    school_id:""+phoneClass.school_id,
                    number:""+phoneClass.number
                )
            }
        }

        jsonResult = new UtilitiesService().fillSchoolResult(newSchool)
        newSchool.save(flush:true)
        jsonResult
    }

    def deleteSchool(def params,def dominio){
        Map jsonResult = [:]
        def schoolResult

        if(!params.school_id){
             throw new NotFoundException("Invalid URL, missing user_id")
        }

        schoolResult = School.findBySchool_id(params.school_id)
        
        new UtilitiesService().existSchool(schoolResult,params.school_id)

        jsonResult.comment = "The school with the school_id "+params.school_id+" has been deleted successfully"
        schoolResult.delete(flush:true)

        jsonResult
    }

    def getSchools(def dominio){
        def result
        def schools = School.createCriteria()
        def escuelasList
        
        if(!schools){
            throw new NotFoundException("Empty schools collection")
        }

        escuelasList = schools.list(){}
        escuelasList
    }
}
