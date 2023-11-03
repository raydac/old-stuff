/*
* ============================================================================
*  Name     : CSmartImageViewSettings from SmartImageView.h
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Description:
*     Declares view for application.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

#ifndef SMARTIMAGEVIEW_H
#define SMARTIMAGEVIEW_H

// INCLUDES
#include <aknview.h>


// CONSTANTS
// UID of view
const TUid KViewId = {1};

// FORWARD DECLARATIONS
class CSmartImageContainerSettings;

// CLASS DECLARATION

/**
*  CSmartImageViewSettings view class.
* 
*/
class CSmartImageViewSettings : public CAknView
    {
    public: // Constructors and destructor

        /**
        * EPOC default constructor.
        */
        void ConstructL();

        /**
        * Destructor.
        */
        ~CSmartImageViewSettings();

    public: // Functions from base classes
	    void ShowCurrentL();
        
        /**
        * From ?base_class ?member_description
        */
        TUid Id() const;

        /**
        * From ?base_class ?member_description
        */
        void HandleCommandL(TInt aCommand);

        /**
        * From ?base_class ?member_description
        */
        void HandleClientRectChange();

    private:

        /**
        * From AknView, ?member_description
        */
        void DoActivateL(const TVwsViewId& aPrevViewId,TUid aCustomMessageId,
            const TDesC8& aCustomMessage);

        /**
        * From AknView, ?member_description
        */
        void DoDeactivate();

    private: // Data
        CSmartImageContainerSettings* iContainer;
    };

#endif

// End of File
