############ MASTER TARGETS ###########
all: compile-swipl bootstrap compile-chemlogic

.SUFFIXES: 

##### EXTRACT REQUIRED BUILD FILES ######
extract: build/pl-6.6.6 build/chemlogic-1.00_2 build/angstrom.rootfs build/toolchain 

build/pl-6.6.6: chemlogic-distfiles/pl-6.6.6.tar.gz
	tar -xf $< -C build
	touch $@

build/build-pl-6.6.6: build/pl-6.6.6
	mkdir -p $@
	cp -a ${<}/* $@

build/chemlogic-1.00_2: chemlogic-distfiles/chemlogic-1.00_2.tar.gz
	tar -xf $< -C build
	touch $@

build/angstrom.rootfs: chemlogic-distfiles/Angstrom-systemd-image-eglibc-ipk-v2014.06-qemuarm.rootfs.tar.xz
	mkdir -p $@
	tar -xf $< -C $@
	touch $@


build/toolchain: chemlogic-distfiles/angstrom-2011.03-x86_64-linux-armv5te-linux-gnueabi-toolchain.tar.bz2
	mkdir -p $@
	tar -xf $< -C $@
	touch $@

##### COMPILE SWI-PROLOG FOR X86_64 (BUILD COMPILER) ####
HOST_SWIPL_PREFIX = `readlink -f ../../swipl-host`

compile-build-swipl: build/build-pl-6.6.6
	cd build/build-pl-6.6.6; \
	./configure --prefix ${HOST_SWIPL_PREFIX}; \
	make; \
	make install; \
	export PATH=${HOST_SWIPL_PREFIX}/bin/:${PATH}; \
	cd packages/clpqr/; \
	./configure; \
	make install


##### COMPILE SWI-PROLOG FOR ARM #####

compile-swipl: extract build/pl-6.6.6/src/configure splice-buildtools
	export CC=`readlink -f build/toolchain/usr/local/angstrom/arm/arm-angstrom-linux-gnueabi/bin/gcc`; \
	cd build/pl-6.6.6; \
	./configure --host arm-angstrom-linux-gnueabi; \
	make || echo "Make is stopped. SWI-Prolog must be bootstrapped on an ARM processor."



##### PATCHES, SPLICES AND FIXES FOR SWI-PROLOG ######
build/pl-6.6.6/src/configure: patch/swipl-6-configure build/pl-6.6.6/src/configure.in
	cd build/pl-6.6.6/src; \
	patch < ../../../$<; \
	autoreconf

#.PHONY: build/pl-6.6.6/src/defatom build/pl-6.6.6/src/mkvmi
splice-buildtools: build/pl-6.6.6/src/defatom build/pl-6.6.6/src/mkvmi

build/pl-6.6.6/src/defatom: build/pl-6.6.6/src/defatom.c
	cd build/pl-6.6.6/src; \
	$(CC) -Wall -O2  -fno-strict-aliasing -pthread -fPIC  -o defatom ./defatom.c

	
build/pl-6.6.6/src/mkvmi: build/pl-6.6.6/src/mkvmi.c
	cd build/pl-6.6.6/src; \
	$(CC) -g -O2 -o mkvmi ./mkvmi.c


##### MAKE ANDROID FILESYSTEM TREE #####



ANGSTROM_LIBS = ld-2.19-2014.04.so ld-linux.so.3 libc.so.6 libdl.so.2 libgcc_s.so.1 libm.so.6 libpthread.so.0 librt.so.1
ANGSTROM_LIBS_SRC = $(addprefix build/angstrom.rootfs/lib/, ${ANGSTROM_LIBS})
ANGSTROM_LIBS_DEST = $(addprefix buildout/angstrom/lib/, ${ANGSTROM_LIBS})

androidfs-bootstrap: mkdir copy-angstrom-libs buildout/angstrom/pl/libgmp.so.10 buildout/angstrom/pl/swipl buildout/angstrom/pl/libswipl.so.6.6.6 buildout/angstrom/plboot/ buildout/angstrom/bin/busybox.nosuid buildout/angstrom/etc/init.bootstrap buildout/angstrom/etc/init buildout/angstrom/etc/rc.bootstrap buildout/angstrom/etc/rc


buildout/angstrom/pl/libgmp.so.10: build/toolchain/usr/local/angstrom/arm/arm-angstrom-linux-gnueabi/usr/lib/libgmp.so.10
	cp $< $@
buildout/angstrom/pl/swipl: build/pl-6.6.6/src/swipl
	cp $< $@
buildout/angstrom/pl/libswipl.so.6.6.6: build/pl-6.6.6/lib/arm-linux/libswipl.so.6.6.6
	cp $< $@
buildout/angstrom/etc/init.bootstrap: scripts/init.bootstrap
	cp -a $< $@
buildout/angstrom/etc/init: scripts/init
	cp -a $< $@
buildout/angstrom/etc/rc.bootstrap: scripts/rc.bootstrap
	cp -a $< $@
buildout/angstrom/etc/rc: scripts/rc
	cp -a $< $@
copy-angstrom-libs: 
	cp ${ANGSTROM_LIBS_SRC} buildout/angstrom/lib/
	




buildout/angstrom/plboot/: build/pl-6.6.6/boot/
	cp -r $< $@
 
buildout/angstrom/bin/busybox.nosuid:  build/angstrom.rootfs/bin/busybox.nosuid
	cp $< $@
	cd buildout/angstrom/bin; \
	ln -s busybox.nosuid sh




DIRS = bin lib pl etc chemlogic 
DIRS_PATH = $(addprefix buildout/angstrom/, ${DIRS})
mkdir: ${DIRS_PATH} 
${DIRS_PATH}:
	- mkdir ${DIRS_PATH}

###### BOOTSTRAP PROLOG USING ANDROID EMULATOR ######

emulator:
	@echo "Starting Android emulator. The system will appear to hang."
	emulator @chemlogic-angstrom-bootstrap &

pushfs: androidfs-bootstrap emulator
	@ echo "Waiting to push files to Android. The system may appear to hang."
	adb wait-for-device
	- adb shell rm -r /data/local/chemlogic-bootstrap/
	adb push -p buildout/ /data/local/chemlogic-bootstrap/


bootstrap-swipl: pushfs
	adb shell /data/local/chemlogic-bootstrap/angstrom/etc/init.bootstrap

buildout/angstrom/pl/swipl.prc: bootstrap-swipl 
	adb pull -p /data/local/chemlogic-bootstrap/angstrom/pl/swipl.prc $@

bootstrap: buildout/angstrom/pl/swipl.prc halt-emulator clean-bootstrap

halt-emulator:
	-adb emu kill

clean-bootstrap:
	rm -r buildout/angstrom/plboot
	rm    buildout/angstrom/etc/*.bootstrap

###### COMPILE CHEMLOGIC ######
compile-chemlogic buildout/angstrom/chemlogic/chemcli: build/chemlogic-1.00_2
	cd  $<; \
	make cli DEST=../../buildout/angstrom/chemlogic/



###### CLEAN BUILD WORK ###########
clean:
	- rm -r build/*
	- rm -r buildout/angstrom/*
	- rm -r swipl-host/*

