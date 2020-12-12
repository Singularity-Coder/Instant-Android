# MVC ARCHI NOTES

MVC stands for Model View Controller. All architectures are just different flavors of MVC Architecture.

M – Model: Its what your App is. It’s the data access class. Business Logic will be here. It provides access to the data. Has a connection with “Controller” and does not speak directly with the “View”.
V – View: Its how your App looks. View bindings, Click events, etc. Has a connection with “Controller” and does not speak directly with the “Model”.
C – Controller: Its what your App does. This is the one that gets the data from the “Model” and sets it to the “View”. It’s controls App behavior. What the “View” needs it delivers to it from the “Model”.