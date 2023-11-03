/*
* ============================================================================
*  Name     : CSmartImageDocument from SmartImageDocument.h
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Description:
*     Declares document for application.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

#ifndef SMARTIMAGEDOCUMENT_H
#define SMARTIMAGEDOCUMENT_H

// INCLUDES
#include <akndoc.h>
   
// CONSTANTS

// FORWARD DECLARATIONS
class  CEikAppUi;

// CLASS DECLARATION

/**
*  CSmartImageDocument application class.
*/
class CSmartImageDocument : public CAknDocument
    {
    public: // Constructors and destructor
        /**
        * Two-phased constructor.
        */
        static CSmartImageDocument* NewL(CEikApplication& aApp);

        /**
        * Destructor.
        */
        virtual ~CSmartImageDocument();

    public: // New functions

    protected:  // New functions

    protected:  // Functions from base classes

    private:

        /**
        * EPOC default constructor.
        */
        CSmartImageDocument(CEikApplication& aApp);
        void ConstructL();

    private:

        /**
        * From CEikDocument, create CSmartImageAppUi "App UI" object.
        */
        CEikAppUi* CreateAppUiL();
    };

#endif

// End of File

