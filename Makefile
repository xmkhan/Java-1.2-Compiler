OBJECTS = 

assign2: ${OBJECTS}
run: joosc
	java
clean:
	rm -rf *.class

.class: .java
	javac
