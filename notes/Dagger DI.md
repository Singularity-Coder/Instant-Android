# Room Persistence Library Notes

* Dependency here means reliance on another. Ex: ClassA and ClassB. If ClassB depends on ClassA then ClassA is the dependency of ClassB.
* Injection here means passing the created object into the class from outside the class. Ex: Through constructor.
* It's like designing an object through the constructor. 
* Dependency injection creates modular customizable objects.   
* DI makes each class have fewer responsibilities.
* The idea is that classes shouldn’t be responsible for creating their own dependencies or searching for them. Instead, these dependencies should be instantiated from another place and pass them to the classes that need them. 
Problems without DI - 
1. Large bloated classes 
2. Hard or impossible to unit test if dependencies are instantiated here. Hard to mock the behaviour. 
Dagger 2 is DI framework 
It helps getting the instance from one place to another 
Delegtaes object creation or instance creation to another 
Rule of thumb - always pass dependecy through constructor - if not possible do field injection 
Inversion of control - reverse te dependency creation process 
Create custom annotations in java 
Visualise DI
Provider of dependency - creates dependecies - module consumer requires
Consumer of dependency - any class the expresses consumption through inject annotation - annotations r part of java standard lib. 
Facilitator - connects provider to consumer - component 
How dagger 2 works
Provider provides the dependency to the facilitator n it will forward it to the necessary consumer with the inject annotation
What is an annotation?
Create annotation
Create interface to create annotation in java. In kotlin u need keyword annotation. 
Annotation is meta data
What is Meta information - annotation helps identify method variable class info during run time with the annotation processor. 
Annotation is a description of what the class is. 
Basically annotations help identify a piece of code at run time. 
Explore butterknife to explore annotation
There r 4 main annotations
@module - helps identify those classes with provides dependency. Module uses provides to define what will be provided to the consumer. Annotated on class. Any class annotated with @module is the dependency provider.  
@component - takes a module n knows the consumer needs some instances. Component is a facilitator for getting an instance through the module to any class. Its like a bridge btw provider n consumer. This is annoyed on an interface. Component holds all the modules or dependency providers in it. In component u have 2 types of methods. One taht takes a class as a param n other that returns an instance of a class. An instance is an object of a class. All new keywords create instances of a class. Component is a holder of dependencies n stores them in a particular scope. 
@provides - used in module to help identify what is being provided - annotated on method inside the component. 
@inject - annotated on a constructor or a field. This triggers the component to query all the modules declared in it n provides the dependency to the filed or constructor. All classes that consit of inject annotation are consumers.  
@scope - similar to class n method scope of a variable but for dagger activity. Scope provides the same instnace of the component. Same  Instnace is the key word. 
