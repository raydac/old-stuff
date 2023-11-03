/*
* ============================================================================
*  Name     : CSmViewerContainer2 from SmViewerContainer2.h
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

#ifndef SMVIEWERCONTAINER2_H
#define SMVIEWERCONTAINER2_H

// INCLUDES
#include <palbimageviewerbasic.h> 
#include "SmViewerAppUi.h"

 
// FORWARD DECLARATIONS
class CSmViewerView2;

// CLASS DECLARATION

/**
*  CSmViewerContainer2  container control class.
*  
*/
class CSmViewerContainer2 : public CCoeControl
    {
    public: // Constructors and destructor
        
        /**
        * EPOC default constructor.
        * @param aRect Frame rectangle for container.
        */
        void ConstructL(const TRect& aRect,CSmViewerAppUi* aAppUi,CPAlbImageViewerBasic* aViewer,CSmViewerView2* iView);


        /**
        * Destructor.
        */
        ~CSmViewerContainer2();


    public: // New functions

    public: // Functions from base classes
/**
	 Zoom in
	 \return nothing
*/
		void ZoomIn();
/**
	 Zoom out
	 \return nothing
*/
		void ZoomOut();
/**
	 Zoom normal
	 \return nothing
*/
		void ZoomNormal();
/**
	 rotate right
	 \return nothing
*/
		void RotateRight();
/**
	 rotate left
	 \return nothing
*/
		void RotateLeft();
/**
	 set title
	 \return nothing
*/
		void SetTitleL(TDesC& aFileName);
/**
	 set caption
	 \return nothing
*/
		void SetCaptionL();
/**
	 activate previously image
	 \return nothing
*/
		TInt ActivatePrevL();
/**
	 activate next image
	 \return nothing
*/
		TInt ActivateRandomL();
/**
	 activate random image
	 \return nothing
*/
		TInt ActivateNextL();
/**
	 activate image by index
	 \return nothing
*/
		TBool ActivatePictureL(TDesC& aFileName);

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
		TKeyResponse  OfferKeyEventL(const TKeyEvent& aKeyEvent,TEventCode aType );

// members:	
	public:
//		void SetLabel();
/**
	 activate slide show
	 \return nothing
*/
		void ActivateSlideShow();
/**
	 deactivate slide show
	 \return nothing
*/
		TBool DeActivateSlideShow();

		CPAlbImageViewerBasic* iViewer;
		CSmViewerView2* iView;

    private: //data
	    TInt SliderRun();
	    static TInt SliderRun(TAny *aThis);
		CSmViewerAppUi* iAppUi;
//		CEikLabel* iLabel;        
		CAknNavigationDecorator* iNaviDecorator;
		CPeriodic* iSlider;
		TBool iSliderFirstRun;
		TRect originalRect;


    protected:
	    void SetSize(TBool aFull);
    };

#endif

// End of File
