/*
* ============================================================================
*  Name     : CSmViewerApp from SmViewerApp.h
*  Part of  : SmViewer
*  Created  : 05/09/2003 by 
* ============================================================================
*/
/* ========================================================================
  * Author:  Oleg Golosovskiy, http://www.sw4u.org mailto:mail@sw4u.org
  * ======================================================================== */
/** 
  * \author Oleg Golosovsky     
  */


#ifndef SMVIEWERAPP_H
#define SMVIEWERAPP_H

// INCLUDES
#include <aknapp.h>

// CONSTANTS
// UID of the application
const TUid KUidSmViewer = { 0x0776B005 };

// CLASS DECLARATION

/**
* CSmViewerApp application class.
* Provides factory to create concrete document object.
* 
*/
class CSmViewerApp : public CAknApplication
    {
    
    public: // Functions from base classes
    private:

        /**
        * From CApaApplication, creates CSmViewerDocument document object.
        * @return A pointer to the created document object.
        */
        CApaDocument* CreateDocumentL();
        
        /**
        * From CApaApplication, returns application's UID (KUidSmViewer).
        * @return The value of KUidSmViewer.
        */
        TUid AppDllUid() const;
    };

#endif

// End of File

