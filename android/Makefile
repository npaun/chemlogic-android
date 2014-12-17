.PHONY: app install app-clean

app:
	ant debug

install:
	@echo "Please wait... the command may appear to hang."
	adb wait-for-device
	adb install -r bin/Chemlogic-debug.apk

app-clean:
	ant clean


app-emulator:
	@echo "Please wait... the command may appear to hang."
	emulator @chemlogic-dev &

