package com.api.cendi

class Phone {

	String school_id
	String number

    static constraints = {
    	school_id		(nullable:false, blank:false, unique:false)
		number			(nullable:false, blank:false, unique:false)
    }
}
