ANDROID = $(MAKE) -C android
SYSTEM = $(MAKE) -C system

.PHONY: app system all clean 

default: app install


app app-clean app-emulator install:
	$(ANDROID) $@

system system-clean system-install: 
	$(SYSTEM) $@

all: app system	


clean: app-clean system-clean 
