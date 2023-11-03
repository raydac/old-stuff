/*
* ============================================================================
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

#ifndef __SMVIEWERCONTAINERSETTINGS_H__
#define __SMVIEWERCONTAINERSETTINGS_H__

#include <aknview.h>

// INCLUDES
#include <aknsettingitemlist.h>
#include "SmViewerAppUi.h"

   
// FORWARD DECLARATIONS

// CLASS DECLARATION

class CSmvSettingItemList : public CAknSettingItemList
	{
public:
	CSmvSettingItemList(TSmViewerSettings& aData);

protected:
	virtual CAknSettingItem* CreateSettingItemL(TInt aSettingId);

private:
	TSmViewerSettings& iData;
	};

/**
*  CSmartImageContainerSettings  container control class.
*  
*/
class CSmViewerContainerSettings : public CCoeControl, MCoeControlObserver
    {
    public: // Constructors and destructor

		CSmViewerContainerSettings();
        void ConstructL(CSmViewerAppUi* aAppUi, const TRect& aRect);
        ~CSmViewerContainerSettings();
    public: 
		// from base class overload
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
	
	CSmViewerAppUi* iAppUi;
};


// End of File
#endif // __SMVIEWERCONTAINERSETTINGS_H__

