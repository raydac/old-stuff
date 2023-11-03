/*
* ============================================================================
*  Name     : CSmViewerContainer2 from SmViewerContainer2.cpp
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

#ifndef __SMVIEWERCONTAINER3_H__
#define __SMVIEWERCONTAINER3_H__

#include <aknview.h>
#include <akncontext.h>
#include <mdaimageconverter.h>
#include "SmViewerAppUi.h"
#include <aknlists.h>
#include <eiklbo.h>

struct TContactInfo
{
	TParse iFileName;
	TName  iText;
	TName  iContactName;
	TName  iTelephone;
	TBool  iLinked;
	TInt iImageIndex;
	TInt iAltImageIndex;
	HBufC8* iThumbail;
	TContactInfo::TContactInfo()
	{
		iImageIndex =0;// no image
		iLinked = EFalse;
		iThumbail=0;
		iAltImageIndex=0;
	}


};

class CSmViewerContainer3;


/**
*  TContactInfo array wrapper
*  
*/
class CListContactsArray : public CArrayFixFlat<TContactInfo>, public MDesC16Array
{
public:
	CListContactsArray(TInt aGranularity);
	IMPORT_C TInt MdcaCount() const;
	IMPORT_C TPtrC16 MdcaPoint(TInt aIndex) const;
};

/**
	additional class for load tmumb 
*/

class CSmViewerThunbLoader : public CActive, MMdaImageUtilObserver 
{
public:
	friend CSmViewerContainer3;

	CSmViewerThunbLoader(CSmViewerContainer3* aCont);
	~CSmViewerThunbLoader();
// MMdaImageUtilObserver
		void MiuoCreateComplete(TInt aError);
		void MiuoOpenComplete(TInt aError);
		void MiuoConvertComplete(TInt aError);
	void DoCancel();
	void RunL();
	void Start();

private:

	TInt iCurrentThumbail;
	TBool iConverting;
	CMdaImageDescToBitmapUtility* iConverter;
	TInt iConverterType;
	CMdaBitmapScaler* iScaler;
	TBool iScaling;

	CSmViewerContainer3* iCont;

};

class MSmViewerThunbSaverObserver
	{
public:
	virtual void AttachSecondPhaseL() = 0;
	};

/**
	additional class for save tmumb 
*/

class CSmViewerThunbSaver : public CActive, MMdaImageUtilObserver 
{
public:
	friend CSmViewerContainer3;
	CSmViewerThunbSaver(CSmViewerAppUi* aAU,CFbsBitmap* aBitmap,HBufC8* aBuffer,MSmViewerThunbSaverObserver* aObserver);
	~CSmViewerThunbSaver();
// MMdaImageUtilObserver
		void MiuoCreateComplete(TInt aError);
		void MiuoOpenComplete(TInt aError);
		void MiuoConvertComplete(TInt aError);
	void DoCancel();
	void RunL();
	void Start();

private:

	TInt iCurrentThumbail;
	TBool iConverting;
	CMdaImageBitmapToDescUtility* iConverter;
	TInt iConverterType;
	MSmViewerThunbSaverObserver* iObserver;

	CSmViewerAppUi* iUI;
	CFbsBitmap* iBitmap;
	TMdaJfifClipFormat jfifFormat;
	TPtr8* iPtr;
public:
	HBufC8* iBuffer;

};

/**
*  CSmViewerContainer3  container control class for contacts listbox
*  
*/

class CSmViewerContainer3 : public CCoeControl, MEikListBoxObserver, MSmViewerThunbSaverObserver
    {

public: 

	friend CSmViewerThunbLoader;
	friend CSmViewerThunbSaver;


	CSmViewerContainer3();


/*!
  @fuction ConstructL
  
  @discussion Perform the second phase construction of a CSmViewerContainer3 object
  @param aRect Frame rectangle for container.
  */
    void ConstructL(const TRect& aRect,CSmViewerAppUi* aAppUi);

	~CSmViewerContainer3();

public: // from CoeControl
	    void Sorting();
	    TInt UpdateImages(CListContactsArray* aArray);
		void AttachL();
		void AttachSecondPhaseL();
		void DeAttachL();
/*!
  @function CountComponentControls
  
  @return Number of component controls 
  */
    TInt CountComponentControls() const;

/*!
  @function ComponentControl.

  @param Specification for component pointer
  @return Pointer to component control
  */
    CCoeControl* ComponentControl(TInt aIndex) const;

/*!
  @function Draw
  
  @discussion Draw this CSmViewerContainer3 to the screen
  @param aRect the rectangle of this view that needs updating
  */

    
	void Draw(const TRect& aRect) const;

	// from base class
	void SizeChanged();
	TKeyResponse  OfferKeyEventL(const TKeyEvent& aKeyEvent,TEventCode aType );
	void HandleListBoxEventL(CEikListBox* aListBox, TListBoxEvent aEventType);

/**
	create text string for usage from listbox control
  */
	void CreateText(TContactInfo& aInfo);
/**
	update caption and navigator bar text information
  */
	void SetCaptionL();

protected:
/**
	create and adjust scroll bar
  */
	void CreateScrollbarL();
private:

    CAknDoubleLargeStyleListBox* iListBox;    
	CListContactsArray*	iTextArray;
	CSmViewerAppUi* iAppUi;
	CAknNavigationDecorator* iNaviDecorator;
	CAknContextPane* iContextPane;
	TContactInfo iImgInfo;

	CSmViewerThunbLoader* iLoader;
	CSmViewerThunbSaver* iSaver;

	TInt oldImageIndex;

};

#endif // __SMVIEWERCONTAINER3_H__

