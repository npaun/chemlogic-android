.PHONY: app install app-clean

app:
	cd android; \
	ant debug

install:
	@echo "Please wait... the command may appear to hang."
	adb install -r android/bin/Chemlogic-debug.apk

app-clean:
	cd android; \
	ant clean


run-emu:
	@echo "Please wait... the command may appear to hang."
	emulator @droid-phone-4l &

