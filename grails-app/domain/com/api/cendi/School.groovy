package com.api.cendi

class School {

	String school_id
	String name
	String street
	String number
	String neighborhood
	String postal_code
	String principal
	Date opening_date

	List phones = []

    static constraints = {
    	school_id 		(nullable:false,blank:false,unique:true)
    	name 			(nullable:false,blank:false,unique:false)
		street			(nullable:false,blank:false,unique:false)
		number			(nullable:false,blank:false,unique:false)
		neighborhood	(nullable:false,blank:false,unique:false)
		postal_code		(nullable:false,blank:false,unique:false)
		principal		(nullable:false,blank:false,unique:false)
    	phones			(nullable:false,blank:true,unique:false)
    }
}
