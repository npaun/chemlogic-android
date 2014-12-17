############ CONFIGURATION ###########

include distfiles/Distinfo.mk

# Compile for ARM devices (most Android devices)
HOST = arm
# Compile on an x86_64 machine
BUILD = x86_64

# Select the correct architecture-dependent files
ROOTFS = rootfs.host-$(HOST)
TOOLCHAIN = toolchain.host-$(HOST).build-$(BUILD)


############ MASTER TARGETS ###########
all: compile-swipl bootstrap

.SUFFIXES: 

##### EXTRACT REQUIRED BUILD FILES ######
extract: build build/$(swipl.dir) build/$(chemlogic.dir) build/$($(ROOTFS).dir) build/$($(TOOLCHAIN).dir)

build:
	mkdir -p $@

build/$(swipl.dir): distfiles/$(swipl.file)
	tar -xf $< -C build
	touch $@

build/build-$(swipl.dir): build/$(swipl.dir)
	mkdir -p $@
	cp -a ${<}/* $@

build/$(chemlogic.dir): distfiles/$(chemlogic.file)
	tar -xf $< -C build
	touch $@

build/$($(ROOTFS).dir): distfiles/$($(ROOTFS).file)
	mkdir -p $@
	tar -xf $< -C $@
	touch $@


build/$($(TOOLCHAIN).dir): distfiles/$($(TOOLCHAIN).file)
	mkdir -p $@
	tar -xf $< -C $@
	touch $@


##### COMPILE SWI-PROLOG FOR HOST SYSTEM (ANDROID DEVICE) #####

compile-swipl: extract build/$(swipl.dir)/src/configure splice-buildtools
	export CC=`readlink -f build/$($(TOOLCHAIN).dir)/usr/local/angstrom/$(HOST)/$(HOST)-angstrom-linux-gnueabi/bin/gcc`; \
	cd build/$(swipl.dir); \
	./configure --host $(HOST)-angstrom-linux-gnueabi; \
	make || echo "Make is stopped. SWI-Prolog must be bootstrapped on an $(HOST) processor."; \


##### PATCHES, SPLICES AND FIXES FOR SWI-PROLOG ######

# This patch is probably version-specific
build/pl-6.6.6/src/configure: patch/swipl-6-configure build/pl-6.6.6/src/configure.in
	cd build/pl-6.6.6/src; \
	patch < ../../../$<; \
	autoreconf

splice-buildtools: build/$(swipl.dir)/src/defatom build/$(swipl.dir)/src/mkvmi

build/$(swipl.dir)/src/defatom: build/$(swipl.dir)/src/defatom.c
	cd build/$(swipl.dir)/src; \
	$(CC) -Wall -O2  -fno-strict-aliasing -pthread -fPIC  -o defatom ./defatom.c

	
build/$(swipl.dir)/src/mkvmi: build/$(swipl.dir)/src/mkvmi.c
	cd build/$(swipl.dir)/src; \
	$(CC) -g -O2 -o mkvmi ./mkvmi.c


##### MAKE ANDROID FILESYSTEM TREE #####



ANGSTROM_LIBS = ld-linux.so.3 libc.so.6 libdl.so.2 libgcc_s.so.1 libm.so.6 libpthread.so.0 librt.so.1
ANGSTROM_LIBS_SRC = $(addprefix build/$($(ROOTFS).dir)/lib/, ${ANGSTROM_LIBS})
ANGSTROM_LIBS_DEST = $(addprefix buildout/angstrom/lib/, ${ANGSTROM_LIBS})

androidfs-bootstrap: mkdir copy-angstrom-libs buildout/angstrom/pl/libgmp.so.10 buildout/angstrom/pl/swipl buildout/angstrom/pl/libswipl.so.6.6.6 copy-boot-files buildout/angstrom/bin/busybox.nosuid buildout/angstrom/etc/init.bootstrap buildout/angstrom/etc/init buildout/angstrom/etc/rc.bootstrap buildout/angstrom/etc/rc buildout/angstrom/etc/make.bootstrap


buildout/angstrom/pl/libgmp.so.10: build/$($(TOOLCHAIN).dir)/usr/local/angstrom/$(HOST)/$(HOST)-angstrom-linux-gnueabi/usr/lib/libgmp.so.10
	cp $< $@
buildout/angstrom/pl/swipl: build/$(swipl.dir)/src/swipl
	cp $< $@
buildout/angstrom/pl/libswipl.so.6.6.6: build/$(swipl.dir)/lib/$(HOST)-linux/libswipl.so.6.6.6
	cp $< $@
buildout/angstrom/etc/init.bootstrap: scripts/init.bootstrap
	cp -a $< $@
buildout/angstrom/etc/init: scripts/init
	cp -a $< $@
buildout/angstrom/etc/rc.bootstrap: scripts/rc.bootstrap
	cp -a $< $@
buildout/angstrom/etc/rc: scripts/rc
	cp -a $< $@
buildout/angstrom/etc/make.bootstrap: scripts/make.bootstrap
	cp $< $@
copy-angstrom-libs: 
	cp ${ANGSTROM_LIBS_SRC} buildout/angstrom/lib/
	



copy-boot-files: buildout/angstrom/plboot/swipl buildout/angstrom/plboot/library buildout/angstrom/plboot/library/clpqr buildout/angstrom/plboot/chemlogic
buildout/angstrom/plboot/swipl: build/$(swipl.dir)/boot/
	cp -r $< $@
buildout/angstrom/plboot/library: build/$(swipl.dir)/library/
	cp -r $< $@
buildout/angstrom/plboot/library/clpqr: build/$(swipl.dir)/packages/clpqr/
	cp -r $< $@
buildout/angstrom/plboot/chemlogic: build/$(chemlogic.dir)/
	cp -r $< $@

buildout/angstrom/bin/busybox.nosuid:  build/$($(ROOTFS).dir)/bin/busybox.nosuid
	cp $< $@
	cd buildout/angstrom/bin; \
	ln -s busybox.nosuid sh


DIRS = bin lib pl etc chemlogic plboot 
DIRS_PATH = $(addprefix buildout/angstrom/, ${DIRS})
mkdir: ${DIRS_PATH} 
${DIRS_PATH}:
	- mkdir -p ${DIRS_PATH}

###### BOOTSTRAP PROLOG USING ANDROID EMULATOR ######

emulator:
	@echo "Starting Android emulator. The system will appear to hang."
	emulator @chemlogic-angstrom-bootstrap &

pushfs: androidfs-bootstrap emulator
	@ echo "Waiting to push files to Android. The system may appear to hang."
	adb wait-for-device
	- adb shell rm -r /data/local/chemlogic-bootstrap/
	adb push -p buildout/ /data/local/chemlogic-bootstrap/


run-bootstrap: pushfs
	adb shell /data/local/chemlogic-bootstrap/angstrom/etc/init.bootstrap

buildout/angstrom/pl/swipl.prc:
	adb pull -p /data/local/chemlogic-bootstrap/angstrom/pl/swipl.prc $@

buildout/angstrom/chemlogic/chemcli:
	adb pull -p /data/local/chemlogic-bootstrap/angstrom/chemlogic/chemcli $@
	chmod a+x $@

bootstrap: run-bootstrap buildout/angstrom/pl/swipl.prc buildout/angstrom/chemlogic/chemcli halt-emulator clean-bootstrap

halt-emulator:
	-adb emu kill

clean-bootstrap:
	rm -r buildout/angstrom/plboot
	rm    buildout/angstrom/etc/*.bootstrap



###### CLEAN BUILD WORK ###########
clean:
	- rm -r build/*
	- rm -r buildout/angstrom/*

