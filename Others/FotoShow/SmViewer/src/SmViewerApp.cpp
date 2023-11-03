/*
* ============================================================================
*  Name     : CSmViewerApp from SmViewerApp.cpp
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
#include    "SmViewerApp.h"
#include    "SmViewerDocument.h"

// ================= MEMBER FUNCTIONS =======================

// ---------------------------------------------------------
// CSmViewerApp::AppDllUid()
// Returns application UID
// ---------------------------------------------------------
//
TUid CSmViewerApp::AppDllUid() const
    {
    return KUidSmViewer;
    }

   
// ---------------------------------------------------------
// CSmViewerApp::CreateDocumentL()
// Creates CSmViewerDocument object
// ---------------------------------------------------------
//
CApaDocument* CSmViewerApp::CreateDocumentL()
    {
    return CSmViewerDocument::NewL( *this );
    }

// ================= OTHER EXPORTED FUNCTIONS ==============
//
// ---------------------------------------------------------
// NewApplication() 
// Constructs CSmViewerApp
// Returns: created application object
// ---------------------------------------------------------
//
EXPORT_C CApaApplication* NewApplication()
    {
    return new CSmViewerApp;
    }

// ---------------------------------------------------------
// E32Dll(TDllReason) 
// Entry point function for EPOC Apps
// Returns: KErrNone: No error
// ---------------------------------------------------------
//
GLDEF_C TInt E32Dll( TDllReason )
    {
    return KErrNone;
    }

// End of File  

