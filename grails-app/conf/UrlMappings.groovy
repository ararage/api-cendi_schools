class UrlMappings {

	static mappings = {
        "/"{
			controller = "Schools"
			action = [GET:"notAllowed",POST:"postSchool",PUT:"notAllowed",DELETE:"notAllowed"]
		}

		"/$school_id?"{
			controller = "Schools"
			action = [GET:"getSchool",POST:"notAllowed",PUT:"updateSchool",DELETE:"deleteSchool"]
		}
	}
}
