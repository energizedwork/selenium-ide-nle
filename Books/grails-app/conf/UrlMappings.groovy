class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }
        "/attachment/download/$ownerClass/$ownerId/$ownerField/$filename" {
            controller = 'attachment'
            action = 'download'
        }
        "/"(view:"/index")
        "500"(view:'/error')
	}
}
