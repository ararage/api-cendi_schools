package com.api.cendi

import schools.exceptions.NotFoundException
import schools.exceptions.ConflictException
import schools.exceptions.BadRequestException
import grails.converters.*
import static org.springframework.http.HttpStatus.*
import static org.springframework.http.HttpMethod.*
import grails.plugin.gson.converters.GSON
import org.springframework.dao.OptimisticLockingFailureException
import com.api.cendi.SchoolsService
import javax.servlet.http.HttpServletResponse

class SchoolsController {

    def setHeaders(){
        response.setContentType "application/json; charset=utf-8"
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setHeader("Access-Control-Allow-Headers", "application/json;charset=UTF-8");
    }

    def renderException(def e){
        def statusCode
        def error
        try{
            statusCode  = e.status
            error       = e.error
        }catch(Exception ex){
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            error = "internal_server_error"
        }
        response.setStatus(statusCode)
        def mapExcepction = [
            message: e.getMessage(),
            status: statusCode,
            error: error
        ]
        render mapExcepction as GSON
    }

    def notAllowed(){
        def method = request.method
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
        setHeaders()
        def mapResult = [
            message: "Method $method not allowed",
            status: HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            error:"not_allowed"
        ]
        render mapResult as GSON
    }

    def getSchool() {
        setHeaders()
        def dominio = request.getServerName()+":"+request.getServerPort()
        def result = [:]
        int retryCounter = 0
        int maxretry=15
        boolean needsProcessing = true
        while(needsProcessing && retryCounter < maxretry) {
            try{
                //result = userService.getUser(params,dominio)
                result = new SchoolsService().getSchool(params,dominio)
                response.setStatus(HttpServletResponse.SC_OK)
                render result as GSON
                needsProcessing=false;
            }catch(NotFoundException e){
                needsProcessing=false;
                renderException(e)
            }catch(ConflictException e){
                needsProcessing=false;
                renderException(e)
            }catch (BadRequestException e) {
                needsProcessing=false;
                renderException(e)
            }catch (OptimisticLockingFailureException olfex) {
                if((retryCounter += 1) >= maxretry) renderException(olfex);
            }catch(Exception e){
              println "Users Exception error----> "+e
              needsProcessing=false;
              renderException(e)
            }
        }
    }

    def postSchool(){
        setHeaders()
        def dominio = request.getServerName()+":"+request.getServerPort()
        def result
        int retryCounter = 0
        int maxretry=15
        boolean needsProcessing = true
        while(needsProcessing && retryCounter < maxretry) {
            try{
                println "Entre en el post"
                //result = userService.postUser(request.JSON,dominio)
                result = new SchoolsService().postSchool(request.JSON,dominio)
                response.setStatus( HttpServletResponse.SC_CREATED)
                render result as GSON
                needsProcessing=false;
            }catch(NotFoundException e){
                needsProcessing=false;
                renderException(e)
            }catch(ConflictException e){
                needsProcessing=false;
                renderException(e)
            }catch (BadRequestException e) {
                needsProcessing=false;
                renderException(e)
            }catch (OptimisticLockingFailureException olfex) {
                if((retryCounter += 1) >= maxretry) renderException(olfex);
            }catch(Exception e){
              println "Users Exception error----> "+e
              needsProcessing=false;
              renderException(e)
            }
        }
    } 
}
