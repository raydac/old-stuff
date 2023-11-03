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

#include "SmViewerContainerSettings.h"
#include "SmViewer.hrh"
#include <SmViewer.rsg>



CSmvSettingItemList::CSmvSettingItemList(TSmViewerSettings& aData)
	: iData(aData)
	{
	}

CAknSettingItem* CSmvSettingItemList::CreateSettingItemL(TInt aSettingId)
{
	switch (aSettingId)
		{
	case ESmvSort:
		return new (ELeave) CAknEnumeratedTextPopupSettingItem(
            aSettingId, iData.iListBoxSort);
	case ESmvLoop:
		return new (ELeave) CAknEnumeratedTextPopupSettingItem(
            aSettingId, iData.iSlideCyclic);
	case ESmvDirection:
		return new (ELeave) CAknEnumeratedTextPopupSettingItem(
            aSettingId, iData.iSlideDirection);
	case ESmvDelay:
		return new (ELeave) CAknEnumeratedTextPopupSettingItem(
            aSettingId, iData.iSlideInterval);
	default:
		break;
		}
	return NULL;
}

// ================= MEMBER FUNCTIONS =======================

CSmViewerContainerSettings::CSmViewerContainerSettings()
{

}

void CSmViewerContainerSettings::ConstructL(CSmViewerAppUi* aAppUi, const TRect& aRect)
{
	iAppUi = aAppUi;

    CreateWindowL();

    SetRect(aRect);
    ActivateL();

	iListBox = new (ELeave) CSmvSettingItemList(iAppUi->Settings());
    iListBox->SetMopParent(this);
    iListBox->ConstructFromResourceL(R_SMV_SETTING_ITEM_LIST);

	iListBox->LoadSettingsL();

    iListBox->SetRect(aRect);
    iListBox->ActivateL();
}


CSmViewerContainerSettings::~CSmViewerContainerSettings()
{
	delete iListBox;
}



TInt CSmViewerContainerSettings::CountComponentControls() const
{
	if (iListBox)
		return 1;
	else
		return 0;
}

CCoeControl* CSmViewerContainerSettings::ComponentControl(TInt aIndex) const
{
    switch (aIndex)
        {
        case 0:
			return iListBox;
        default:
            return (CCoeControl*)NULL;
        }
	//return (CCoeControl*)0;
}


void CSmViewerContainerSettings::Draw(const TRect& aRect) const
{
    CWindowGc& gc = SystemGc();
    gc.SetPenStyle(CGraphicsContext::ENullPen);
    gc.SetBrushColor(KRgbGray);
    gc.SetBrushStyle(CGraphicsContext::ESolidBrush);
    gc.DrawRect(aRect);
}

void CSmViewerContainerSettings::HandleControlEventL(
    CCoeControl* /*aControl*/,TCoeEvent /*aEventType*/)
    {
    // TODO: Add your control event handler code here
    }



void CSmViewerContainerSettings::SizeChanged()
{
	if (iListBox)
		iListBox->SetRect(Rect());
}


TKeyResponse CSmViewerContainerSettings::OfferKeyEventL(const TKeyEvent& aKeyEvent, TEventCode aType )
{
	if (iListBox)
		{
		if ((aKeyEvent.iCode == EKeyUpArrow) || (aKeyEvent.iCode == EKeyDownArrow) ||
			(aKeyEvent.iCode == EKeyDevice3))
			{
			// Pass joystick up, down and pressed events to the list box.
			TKeyResponse response = iListBox->OfferKeyEventL(aKeyEvent, aType);

			if ((response == EKeyWasConsumed) && (aKeyEvent.iCode == EKeyDevice3))
				{
				if (iListBox->ListBox()->CurrentItemIndex() == 0)
					{
					iListBox->StoreSettingsL();
					iListBox->LoadSettingsL();
					}
				}
			return response;
			}
		}
	return EKeyWasNotConsumed;
}




void CSmViewerContainerSettings::ShowCurrentL()
{
	iListBox->EditItemL(iListBox->ListBox()->CurrentItemIndex(),EFalse);
}

void CSmViewerContainerSettings::StoreSettings()
{
	iListBox->StoreSettingsL();
}
