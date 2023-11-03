
PATH=C:\Symbian\6.1\Shared\epoc32\gcc\bin;C:\Symbian\6.1\Shared\epoc32\tools;C:\Perl\bin;C:\WINNT\system32;C:\WINNT;C:\WINNT\System32\Wbem;C:\Program Files\doxygen\bin;C:\PROGRA~1\ATT\Graphviz\bin;C:\Program Files\Rational\common;C:\Program Files\Microsoft Visual Studio\Common\Tools\WinNT;C:\Program Files\Microsoft Visual Studio\Common\MSDev98\Bin;C:\Program Files\Microsoft Visual Studio\Common\Tools;C:\Program Files\Microsoft Visual Studio\VC98\bin;C:\PROGRA~1\CVSFOR~1

# EPOC DEFINITIONS

EPOCBLD = \Symbian\6.1\Series60\EPOC32\BUILD\SYMBIAN\SPBPROJECTS\SMLANCHER\REC\SMILAUNCHER\WINS\ #
EPOCTRG = \Symbian\6.1\Series60\EPOC32\RELEASE\WINS\ #
EPOCLIB = \Symbian\6.1\Series60\EPOC32\RELEASE\WINS\ #
EPOCLINK = \Symbian\6.1\Series60\EPOC32\RELEASE\WINS\ #
EPOCSTATLINK = \Symbian\6.1\Series60\EPOC32\RELEASE\WINS\ #

EPOCBLDUDEB = $(EPOCBLD)UDEB
EPOCTRGUDEB = $(EPOCTRG)UDEB
EPOCLIBUDEB = $(EPOCLIB)UDEB
EPOCLINKUDEB = $(EPOCLINK)UDEB
EPOCSTATLINKUDEB = $(EPOCSTATLINK)UDEB

EPOCBLDUREL = $(EPOCBLD)UREL
EPOCTRGUREL = $(EPOCTRG)UREL
EPOCLIBUREL = $(EPOCLIB)UDEB
EPOCLINKUREL = $(EPOCLINK)UDEB
EPOCSTATLINKUREL = $(EPOCSTATLINK)UREL


TRGDIR = Z\SYSTEM\RECOGS

DATADIR = Z\SYSTEM\DATA

LIBRARY :
	@echo WARNING: Not attempting to create "$(EPOCLIB)UDEB\SMILAUNCHER.LIB".
	@echo When exports are frozen in "\SYMBIAN\SPBPROJECTS\SMLANCHER\BWINS\MRECU.DEF", regenerate Makefile.


# REAL TARGET - IMPORT LIBRARY

"$(EPOCLIB)UDEB\SMILAUNCHER.LIB" : "\SYMBIAN\SPBPROJECTS\SMLANCHER\BWINS\MRECU.DEF" MAKEWORKLIBRARY
	lib.exe /nologo /machine:i386 /nodefaultlib /name:"SMILAUNCHER.MDL" /def:"\SYMBIAN\SPBPROJECTS\SMLANCHER\BWINS\MRECU.DEF" /out:"$(EPOCLIB)UDEB\SMILAUNCHER.LIB"
	del "$(EPOCLIB)UDEB\SMILAUNCHER.exp"


MAKEWORKLIBRARY : "\Symbian\6.1\Series60\EPOC32\RELEASE\WINS\UDEB"

"\Symbian\6.1\Series60\EPOC32\RELEASE\WINS\UDEB" :
	@perl -S emkdir.pl "\Symbian\6.1\Series60\EPOC32\RELEASE\WINS\UDEB"


# BUILD - UDEB

LIBS= \
	"$(EPOCLINKUDEB)\EUSER.LIB" \
	"$(EPOCLINKUDEB)\APMIME.LIB" \
	"$(EPOCLINKUDEB)\EFSRV.LIB" \
	"$(EPOCLINKUDEB)\APGRFX.LIB" \
	"$(EPOCLINKUDEB)\APPARC.LIB"

LINK_OBJS= \
	"$(EPOCBLDUDEB)\SMILAUNCHER.obj" \
	"$(EPOCBLDUDEB)\SMILAUNCHER.UID.obj"

STAGE1_LINK_FLAGS="$(EPOCSTATLINKUDEB)\EDLL.LIB" \
 $(LIBS) /nologo /base:"0x70800000" /entry:"_E32Dll" /subsystem:windows /dll /debug \
 /incremental:no /machine:IX86 /nodefaultlib /include:"?_E32Dll@@YGHPAXI0@Z" /out:"$(EPOCBLDUDEB)\SMILAUNCHER.MDL" /WARN:3

PRELINKUDEB : $(LINK_OBJS) "$(EPOCSTATLINKUDEB)\EDLL.LIB" $(LIBS)
	link.exe @<<
		$(STAGE1_LINK_FLAGS) $(LINK_OBJS)
<<
	del "$(EPOCBLDUDEB)\SMILAUNCHER.MDL"
	del "$(EPOCBLDUDEB)\SMILAUNCHER.exp"
	dumpbin /exports /out:"$(EPOCBLDUDEB)\SMILAUNCHER.inf" "$(EPOCBLDUDEB)\SMILAUNCHER.lib"
	del "$(EPOCBLDUDEB)\SMILAUNCHER.lib"
	perl -S makedef.pl -Inffile "$(EPOCBLDUDEB)\SMILAUNCHER.inf" "$(EPOCBLD)SMILAUNCHER.def" 
	del "$(EPOCBLDUDEB)\SMILAUNCHER.inf"
	lib.exe  /nologo /machine:i386 /nodefaultlib /name:"SMILAUNCHER.MDL" /def:"$(EPOCBLD)SMILAUNCHER.def" /out:"$(EPOCBLDUDEB)\SMILAUNCHER.lib"
	del "$(EPOCBLDUDEB)\SMILAUNCHER.lib"
	@echo First-stage link successful


STOPLINKUDEB : DELEXPOBJUDEB
	@echo Stopped the build by removing the export object,
	@echo if present, because the pre-link stage failed


POSTBUILDUDEB : DELEXPOBJUDEB LIBRARY


DELEXPOBJUDEB :
	if exist "$(EPOCBLDUDEB)\SMILAUNCHER.exp" del "$(EPOCBLDUDEB)\SMILAUNCHER.exp"



# BUILD - UREL

LIBS= \
	"$(EPOCLINKUREL)\EUSER.LIB" \
	"$(EPOCLINKUREL)\APMIME.LIB" \
	"$(EPOCLINKUREL)\EFSRV.LIB" \
	"$(EPOCLINKUREL)\APGRFX.LIB" \
	"$(EPOCLINKUREL)\APPARC.LIB"

LINK_OBJS= \
	"$(EPOCBLDUREL)\SMILAUNCHER.obj" \
	"$(EPOCBLDUREL)\SMILAUNCHER.UID.obj"

STAGE1_LINK_FLAGS="$(EPOCSTATLINKUREL)\EDLL.LIB" \
 $(LIBS) /nologo /base:"0x70800000" /entry:"_E32Dll" /subsystem:windows /dll \
 /incremental:no /machine:IX86 /nodefaultlib /include:"?_E32Dll@@YGHPAXI0@Z" /out:"$(EPOCBLDUREL)\SMILAUNCHER.MDL" /WARN:3

PRELINKUREL : $(LINK_OBJS) "$(EPOCSTATLINKUREL)\EDLL.LIB" $(LIBS)
	link.exe @<<
		$(STAGE1_LINK_FLAGS) $(LINK_OBJS)
<<
	del "$(EPOCBLDUREL)\SMILAUNCHER.MDL"
	del "$(EPOCBLDUREL)\SMILAUNCHER.exp"
	dumpbin /exports /out:"$(EPOCBLDUREL)\SMILAUNCHER.inf" "$(EPOCBLDUREL)\SMILAUNCHER.lib"
	del "$(EPOCBLDUREL)\SMILAUNCHER.lib"
	perl -S makedef.pl -Inffile "$(EPOCBLDUREL)\SMILAUNCHER.inf" "$(EPOCBLD)SMILAUNCHER.def" 
	del "$(EPOCBLDUREL)\SMILAUNCHER.inf"
	lib.exe  /nologo /machine:i386 /nodefaultlib /name:"SMILAUNCHER.MDL" /def:"$(EPOCBLD)SMILAUNCHER.def" /out:"$(EPOCBLDUREL)\SMILAUNCHER.lib"
	del "$(EPOCBLDUREL)\SMILAUNCHER.lib"
	@echo First-stage link successful


STOPLINKUREL : DELEXPOBJUREL
	@echo Stopped the build by removing the export object,
	@echo if present, because the pre-link stage failed


POSTBUILDUREL : DELEXPOBJUREL LIBRARY


DELEXPOBJUREL :
	if exist "$(EPOCBLDUREL)\SMILAUNCHER.exp" del "$(EPOCBLDUREL)\SMILAUNCHER.exp"




RECREATEWORKSPACE :
	cd \SYMBIAN\SPBPROJECTS\SMLANCHER\REC
	perl -S makmake.pl -D \SYMBIAN\SPBPROJECTS\SMLANCHER\REC\SMILAUNCHER.MMP VC6

