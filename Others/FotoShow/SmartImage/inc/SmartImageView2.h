/*
* ============================================================================
*  Name     : CSmartImageViewShow from SmartImageView2.h
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Description:
*     Declares view for application.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

#ifndef SMARTIMAGEVIEW2_H
#define SMARTIMAGEVIEW2_H

// INCLUDES
#include <aknview.h>


// CONSTANTS
// UID of view
const TUid KView2Id = {2};

// FORWARD DECLARATIONS
class CSmartImageContainerShow;

// CLASS DECLARATION

/**
*  CSmartImageViewShow view class.
* 
*/
class CSmartImageViewShow : public CAknView
    {
    public: // Constructors and destructor
		TInt iLastCommand;

        /**
        * EPOC default constructor.
        */
        void ConstructL();

        /**
        * Destructor.
        */
        ~CSmartImageViewShow();

    public: // Functions from base classes
	    void ResetView();
	    inline CSmartImageContainerShow* Container()
			{	return iContainer;	}
        
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
        CSmartImageContainerShow* iContainer;
    };

#endif

// End of File
