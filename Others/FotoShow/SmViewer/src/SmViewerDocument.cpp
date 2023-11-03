/*
* ============================================================================
*  Name     : CSmViewerDocument from SmViewerDocument.cpp
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

// INCLUDE FILES
#include "SmViewerDocument.h"
#include "SmViewerAppUi.h"

// ================= MEMBER FUNCTIONS =======================

// constructor
CSmViewerDocument::CSmViewerDocument(CEikApplication& aApp)
: CAknDocument(aApp)    
    {
    }

// destructor
CSmViewerDocument::~CSmViewerDocument()
    {
    }

// EPOC default constructor can leave.
void CSmViewerDocument::ConstructL()
    {
    }

// Two-phased constructor.
CSmViewerDocument* CSmViewerDocument::NewL(
        CEikApplication& aApp)     // CSmViewerApp reference
    {
    CSmViewerDocument* self = new (ELeave) CSmViewerDocument( aApp );
    CleanupStack::PushL( self );
    self->ConstructL();
    CleanupStack::Pop();

    return self;
    }
    
// ----------------------------------------------------
// CSmViewerDocument::CreateAppUiL()
// constructs CSmViewerAppUi
// ----------------------------------------------------
//
CEikAppUi* CSmViewerDocument::CreateAppUiL()
    {
    return new (ELeave) CSmViewerAppUi;
    }

// End of File  
