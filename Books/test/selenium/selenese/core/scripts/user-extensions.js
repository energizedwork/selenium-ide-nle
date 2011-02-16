var extensions = ['constants', 'utils', 'accessors', 'actions', 'locators' ];

for(var i=0, max=extensions.length; i<max; i++) {
	document.write("<script src='/selenium-server/core/scripts/extensions/"
	        + extensions[i] + ".js' type='text/javascript'></script>");
}
