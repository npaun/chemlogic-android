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
system: compile-swipl bootstrap

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
ANGSTROM_LIBS_DEST = $(addprefix buildout/system/lib/, ${ANGSTROM_LIBS})

androidfs-bootstrap: mkdir copy-angstrom-libs buildout/system/lib/libgmp.so.10 buildout/system/lib/libswipl.so.6.6.6 copy-boot-files buildout/system/etc/init.bootstrap buildout/system/etc/init buildout/system/etc/make.bootstrap


buildout/system/lib/libgmp.so.10: build/$($(TOOLCHAIN).dir)/usr/local/angstrom/$(HOST)/$(HOST)-angstrom-linux-gnueabi/usr/lib/libgmp.so.10
	cp $< $@
buildout/system/lib/libswipl.so.6.6.6: build/$(swipl.dir)/lib/$(HOST)-linux/libswipl.so.6.6.6
	cp $< $@
buildout/system/etc/init.bootstrap: scripts/init.bootstrap
	cp -a $< $@
buildout/system/etc/init: scripts/init
	cp -a $< $@
buildout/system/etc/make.bootstrap: scripts/make.bootstrap
	cp $< $@
copy-angstrom-libs: 
	cp ${ANGSTROM_LIBS_SRC} buildout/system/lib/
	



copy-boot-files: buildout/system/build/swipl/bin/swipl buildout/system/build/swipl/boot buildout/system/build/swipl/library buildout/system/build/swipl/library/clpqr buildout/system/build/chemlogic
buildout/system/build/swipl/bin/swipl: build/$(swipl.dir)/src/swipl
	cp $< $@
buildout/system/build/swipl/boot: build/$(swipl.dir)/boot/
	cp -r $< $@
buildout/system/build/swipl/library: build/$(swipl.dir)/library/
	cp -r $< $@
buildout/system/build/swipl/library/clpqr: build/$(swipl.dir)/packages/clpqr/
	cp -r $< $@
buildout/system/build/chemlogic: build/$(chemlogic.dir)/
	cp -r $< $@

DIRS = bin lib etc build build/swipl build/swipl/bin
DIRS_PATH = $(addprefix buildout/system/, ${DIRS})
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
	adb shell /data/local/chemlogic-bootstrap/system/etc/init.bootstrap

buildout/system/bin/chemcli:
	adb pull -p /data/local/chemlogic-bootstrap/system/bin/chemcli $@
	chmod a+x $@

version-tag:
	-rm buildout/system/VERSION-*
	touch buildout/system/VERSION-`../build/versionName`

bootstrap: run-bootstrap buildout/system/bin/chemcli version-tag halt-emulator clean-bootstrap

halt-emulator:
	-adb emu kill

clean-bootstrap:
	rm -r buildout/system/build/*
	rm    buildout/system/etc/*.bootstrap



###### CLEAN BUILD WORK ###########
system-clean:
	- rm -r build/*
	- rm -r buildout/system/*

####### INSTALL CHROOT FILES INTO THE ANDROID APP STRUCTURE #####

system-install:
	- rm -r ../android/assets/files/*	
	cp -a buildout/system ../android/assets/files/
