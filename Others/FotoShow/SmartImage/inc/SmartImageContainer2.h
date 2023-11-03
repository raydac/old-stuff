/*
* ============================================================================
*  Name     : CSmartImageContainerShow from SmartImageContainer2.h
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Description:
*     Declares container control for application.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

#ifndef SMARTIMAGECONTAINER2_H
#define SMARTIMAGECONTAINER2_H

// INCLUDES
#include <eikimage.h>
 
// FORWARD DECLARATIONS
class CSmartImageAppUi;

// CLASS DECLARATION

/**
*  CSmartImageContainerShow  container control class.
*  
*/
class CSmartImageContainerShow : public CCoeControl, MCoeControlObserver
    {
    public: // Constructors and destructor
        
        /**
        * EPOC default constructor.
        * @param aRect Frame rectangle for container.
        */
        void ConstructL(const TRect& aRect);

        /**
        * Destructor.
        */
        ~CSmartImageContainerShow();

		CSmartImageContainerShow(CSmartImageAppUi* aAppUi)
					{ iAppUi = aAppUi;	}

		inline CEikImage* Image()
			{ return iImage;	}


    public: // New functions

    public: // Functions from base classes
	    void DrawText(CWindowGc& aGc,TInt aOffset) const;

    private: // Functions from base classes

       /**
        * From CoeControl,SizeChanged.
        */
        void SizeChanged();

       /**
        * From CoeControl,CountComponentControls.
        */
        TInt CountComponentControls() const;

       /**
        * From CCoeControl,ComponentControl.
        */
        CCoeControl* ComponentControl(TInt aIndex) const;

       /**
        * From CCoeControl,Draw.
        */
        void Draw(const TRect& aRect) const;

       /**
        * From ?base_class ?member_description
        */
        // event handling section
        // e.g Listbox events
        void HandleControlEventL(CCoeControl* aControl,TCoeEvent aEventType);
        
    private: //data
        
		CEikImage* iImage;
		CSmartImageAppUi* iAppUi;
    };

#endif

// End of File
