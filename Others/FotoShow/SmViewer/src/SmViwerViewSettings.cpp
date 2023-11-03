/*
* ============================================================================
*  Name     : CSmViewerView4 from SmViewerView4.h
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


#include <aknviewappui.h>
#include <avkon.hrh>
#include <aknconsts.h>
#include  <SmViewer.rsg>

#include "SmViwerViewSettings.h"
#include "SmViewerContainerSettings.h"


// ---------------------------------------------------------
// CSmartImageViewSettings::ConstructL(const TRect& aRect)
// EPOC two-phased constructor
// ---------------------------------------------------------
//
void CSmViwerViewSettings::ConstructL()
    {
    BaseConstructL( R_SMARTIMAGE_VIEW1 );
    }

// ---------------------------------------------------------
// CSmartImageViewSettings::~CSmartImageViewSettings()
// ?implementation_description
// ---------------------------------------------------------
//
CSmViwerViewSettings::~CSmViwerViewSettings()
    {
    if ( iContainer )
        {
        AppUi()->RemoveFromViewStack( *this, iContainer );
        }

    delete iContainer;
    }

// ---------------------------------------------------------
// TUid CSmartImageViewSettings::Id()
// ?implementation_description
// ---------------------------------------------------------
//
TUid CSmViwerViewSettings::Id() const
    {
    return KView4Id;
    }

// ---------------------------------------------------------
// CSmartImageViewSettings::HandleCommandL(TInt aCommand)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViwerViewSettings::HandleCommandL(TInt aCommand)
    {   
    switch ( aCommand )
        {
        case EAknSoftkeyYes:
            {
				iContainer->ShowCurrentL();
            break;
            }
        case EAknSoftkeyBack:
            {
				((CSmViewerAppUi*)AppUi())->ActivateLastViewL();
            break;
            }
        default:
            {
            AppUi()->HandleCommandL( aCommand );
            break;
            }
        }
    }

// ---------------------------------------------------------
// CSmartImageViewSettings::HandleClientRectChange()
// ---------------------------------------------------------
//
void CSmViwerViewSettings::HandleClientRectChange()
    {
    if ( iContainer )
        {
        iContainer->SetRect( ClientRect() );
        }
    }

// ---------------------------------------------------------
// CSmartImageViewSettings::DoActivateL(...)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViwerViewSettings::DoActivateL(
   const TVwsViewId& /*aPrevViewId*/,TUid /*aCustomMessageId*/,
   const TDesC8& /*aCustomMessage*/)
    {
    if (!iContainer)
        {
        iContainer = new (ELeave) CSmViewerContainerSettings;
        iContainer->SetMopParent(this);
        iContainer->ConstructL((CSmViewerAppUi*)AppUi(),ClientRect() );
        AppUi()->AddToStackL( *this, iContainer );
        } 
   }

// ---------------------------------------------------------
// CSmartImageViewSettings::HandleCommandL(TInt aCommand)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViwerViewSettings::DoDeactivate()
    {
    if ( iContainer )
        {
		iContainer->StoreSettings();
        AppUi()->RemoveFromViewStack( *this, iContainer );
        }
    
    delete iContainer;
    iContainer = NULL;
    }

// End of File
