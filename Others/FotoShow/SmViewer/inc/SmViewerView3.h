/*
* ============================================================================
*  Name     : CSmViewerView3 from SmViewerView3.h
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

#ifndef __SMVIEWERVIEW3_H__
#define __SMVIEWERVIEW3_H__

#include <aknview.h>

class CSmViewerContainer3;
const TUid KView3Id = {3};

/*! 
  @class CSmViewerView3
  
  @discussion An instance of the Application View object for the MultiViews 
  example application
  */
class CSmViewerView3: public CAknView
    {
public:

/*!
  @function NewL
   
  @discussion Create a CSmViewerView3 object
  @result a pointer to the created instance of CSmViewerView3
  */
    static CSmViewerView3* NewL();

/*!
  @function NewLC
   
  @discussion Create a CSmViewerView3 object
  @result a pointer to the created instance of CSmViewerView3
  */
    static CSmViewerView3* NewLC();


/*!
  @function ~CSmViewerView3
  
  @discussion Destroy the object and release all memory objects
  */
    ~CSmViewerView3();

	void DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane);
	
public: // from CAknView

/*!
  @function Id
  
  @result the ID of view
*/
    TUid Id() const;

/*!
  @function HandleCommandL.
  
  @discussion Handles the commands
  @param aCommand Command to be handled
  */
    void HandleCommandL(TInt aCommand);

/*!
  @function DoActivateL
  
  @discussion Creates the Container class object
  @param aPrevViewId The id of the previous view
  @param aCustomMessageId message identifier
  @param aCustomMessage custom message provided when the view is changed
  */
  void DoActivateL(const TVwsViewId& aPrevViewId,
                   TUid aCustomMessageId,
                   const TDesC8& aCustomMessage);

/*!
  @function DoDeactivate
  
  @discussion Removes the container class instance from the App UI's stack and
  deletes the instance
  */
  void DoDeactivate();

private:

/*!
  @function CSmViewerView3
  
  @discussion Perform the first phase of two phase construction 
  */
    CSmViewerView3();

/*!
  @function ConstructL
  
  @discussion  Perform the second phase construction of a CSmViewerView3 object
  */
    void ConstructL();

private:

    /*! @var iContainer container for this view */
    //ContainerClass* iContainer;

    /*! @var iIdentifier identifier for this view */
	CSmViewerContainer3* iContainer;
    };

#endif // __SMVIEWERVIEW3_H__

