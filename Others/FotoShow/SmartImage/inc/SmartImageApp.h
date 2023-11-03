/*
* ============================================================================
*  Name     : CSmartImageApp from SmartImageApp.h
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Description:
*     Declares main application class.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

#ifndef SMARTIMAGEAPP_H
#define SMARTIMAGEAPP_H

// INCLUDES
#include <aknapp.h>

// CONSTANTS
// UID of the application
const TUid KUidSmartImage = { 0x0DBC831E };

// CLASS DECLARATION

/**
* CSmartImageApp application class.
* Provides factory to create concrete document object.
* 
*/
class CSmartImageApp : public CAknApplication
    {
    
    public: // Functions from base classes
        /**
        * From CApaApplication, overridden to enable INI file support.
        * @return A pointer to the dictionary store
        */
    CDictionaryStore* OpenIniFileLC(RFs& aFs) const;
    private:

        /**
        * From CApaApplication, creates CSmartImageDocument document object.
        * @return A pointer to the created document object.
        */
        CApaDocument* CreateDocumentL();
        
        /**
        * From CApaApplication, returns application's UID (KUidSmartImage).
        * @return The value of KUidSmartImage.
        */
        TUid AppDllUid() const;
    };

#endif

// End of File

