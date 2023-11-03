/*
* ============================================================================
*  Name     : CSmartImageContainerSettings from SmartImageContainer.h
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Description:
*     Declares container control for application.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

#ifndef SMARTIMAGECONTAINER_H
#define SMARTIMAGECONTAINER_H

// INCLUDES
#include <aknsettingitemlist.h>
#include "SmartImageAppUi.h"

   
// FORWARD DECLARATIONS

// CLASS DECLARATION


class CSmvSettingItemList : public CAknSettingItemList
	{
public:
	CSmvSettingItemList(TSmiSettings& aData);

protected:
	virtual CAknSettingItem* CreateSettingItemL(TInt aSettingId);

private:
	TSmiSettings& iData;
	};

/**
*  CSmartImageContainerSettings  container control class.
*  
*/
class CSmartImageContainerSettings : public CCoeControl, MCoeControlObserver
    {
    public: // Constructors and destructor

		CSmartImageContainerSettings();
        void ConstructL(CSmartImageAppUi* aAppUi, const TRect& aRect);
        ~CSmartImageContainerSettings();
    public: 
	    void StoreSettings();
	    void ShowCurrentL();
	    void SizeChanged();
        TInt CountComponentControls() const;
        CCoeControl* ComponentControl(TInt aIndex) const;
        void Draw(const TRect& aRect) const;
        void HandleControlEventL(CCoeControl* aControl,TCoeEvent aEventType);
		TKeyResponse OfferKeyEventL(const TKeyEvent& aKeyEvent, TEventCode aType);
		inline CSmvSettingItemList* Settings()
		{
			return iListBox;
		}
        
private:
	CSmvSettingItemList* iListBox;
private: // Data.

	CEikScrollBarFrame* iScrollBarFrame;
	
	CSmartImageAppUi* iAppUi;
};

#endif

// End of File
