ANDROID = $(MAKE) -C android
CHROOT = $(MAKE) -C chroot

.PHONY: app chroot all clean 

default: app install


app app-clean app-emulator install:
	$(ANDROID) $@

chroot chroot-clean: 
	$(CHROOT) $@

all: app chroot	


clean: app-clean chroot-clean 
