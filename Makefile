.PHONY: all
all:
	javac -d . -cp .:junit.jar:hamcrest.jar `find . | grep -i \.java$$`
	java -cp .:junit.jar:hamcrest.jar org.junit.runner.JUnitCore pippin.InstructionTester
