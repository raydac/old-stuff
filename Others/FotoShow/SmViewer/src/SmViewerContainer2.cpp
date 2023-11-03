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


// INCLUDE FILES
#include "SmViewerContainer2.h"
#include "SmViewerView2.h"
#include "SmViewerContainer3.h"
#include <SmViewer.rsg>
#include <akntitle.h>
const TInt KFullScreenWidth = 176;
const TInt KFullScreenHeight = 188;


#include <eiklabel.h>  // for example label control

// ================= MEMBER FUNCTIONS =======================

// ---------------------------------------------------------
// CSmViewerContainer2::ConstructL(const TRect& aRect)
// EPOC two phased constructor
// ---------------------------------------------------------
//
void CSmViewerContainer2::ConstructL(const TRect& aRect,CSmViewerAppUi* aAppUi,CPAlbImageViewerBasic* aViewer,CSmViewerView2* aView)
{
	iNaviDecorator =0;
	iAppUi = aAppUi;
    CreateWindowL();
	iSliderFirstRun=ETrue;


	originalRect = aRect;

	iSlider=0;

	iView = aView;


	iViewer = aViewer;
	iViewer->SetContainerWindowL( *this );

    SetRect(aRect);


	if(iAppUi->iActivateSlideShow)
	{
		ActivateSlideShow();
	}
	else
	{
		TFullName filename;
		iAppUi->GetCurImageName(filename);
		ActivatePictureL(filename);
	}



//	iLabel = new (ELeave) CEikLabel;
//	iLabel->SetTextL(_L("The End"));
//	iLabel->SetExtent( TPoint(0,0),TSize(0,0));

    ActivateL();

}




// Destructor
CSmViewerContainer2::~CSmViewerContainer2()
{

	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("Destructor start"));
	if(iSlider)
		iSlider->Cancel();
	delete iSlider;
	iSlider=0;
	delete iNaviDecorator;
	iNaviDecorator=0;


//	delete iLabel;

//#ifdef _DEBUG
//	if(iViewer->IsBusy())
//		TInt i=0;
//#endif


	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("Destructor end"));
	iAppUi=0;
	
}

// ---------------------------------------------------------
// CSmViewerContainer2::SizeChanged()
// Called by framework when the view size is changed
// ---------------------------------------------------------
//
void CSmViewerContainer2::SizeChanged()
{

	TSize bottom;
	TSize win;

//	if(iLabel->Text()->Length()>0)
//		bottom = (iLabel->MinimumSize());
//	else
//		bottom = TSize(0,0);

   win = (Rect().Size());

//   iLabel->SetPosition( TPoint(10,win.iHeight-iLabel->Rect().Size().iHeight));
   if(iViewer)
	   iViewer->SetExtent( TPoint(0,0),win);
}

// ---------------------------------------------------------
// CSmViewerContainer2::CountComponentControls() const
// ---------------------------------------------------------
//
TInt CSmViewerContainer2::CountComponentControls() const
    {
    return 1; // return nbr of controls inside this container
    }

// ---------------------------------------------------------
// CSmViewerContainer2::ComponentControl(TInt aIndex) const
// ---------------------------------------------------------
//
CCoeControl* CSmViewerContainer2::ComponentControl(TInt aIndex) const
    {
    switch ( aIndex )
        {
        case 0:
            return iViewer;
//        case 1:
  //          return iLabel;
        default:
            return NULL;
        }
    }

// ---------------------------------------------------------
// CSmViewerContainer2::Draw(const TRect& aRect) const
// ---------------------------------------------------------
//
void CSmViewerContainer2::Draw(const TRect& aRect) const
    {
    CWindowGc& gc = SystemGc();
    // TODO: Add your drawing code here
    // example code...
    gc.SetPenStyle(CGraphicsContext::ENullPen);
    gc.SetBrushColor(KRgbGray);
    gc.SetBrushStyle(CGraphicsContext::ESolidBrush);
    gc.DrawRect(aRect);
    }



TKeyResponse  CSmViewerContainer2::OfferKeyEventL(const TKeyEvent& aKeyEvent,TEventCode aType )
{
    if ( aType != EEventKey ) // Is not key event?
        {
        return EKeyWasNotConsumed;
        }

	if(DeActivateSlideShow())
		return EKeyWasConsumed;

	if (iAppUi->IsDisplayingMenuOrDialog()) // Check whether wait note is displayed.
		return EKeyWasConsumed;

    if ( iViewer )
	  switch ( aKeyEvent.iCode )
        {
        case EKeyUpArrow:
			{
				TSize vis = iViewer->VisibleSize();
				TSize rect = Rect().Size();
				if((vis.iHeight<rect.iHeight))
					ActivatePrevL();
				else
					iViewer->PanUp();
			}
            return EKeyWasConsumed;
        case EKeyDownArrow:
			{
				TSize vis = iViewer->VisibleSize();
				TSize rect = Rect().Size();
				if((vis.iHeight<rect.iHeight))
					ActivateNextL();
				else
					iViewer->PanDown();
			}
            return EKeyWasConsumed;
        case EKeyLeftArrow:
			{
				TSize vis = iViewer->VisibleSize();
				TSize rect = Rect().Size();
				if((vis.iWidth<rect.iWidth))
					ActivatePrevL();
				else
					iViewer->PanLeft();
			}
            return EKeyWasConsumed;
        case EKeyRightArrow:
			{
				TSize vis = iViewer->VisibleSize();
				TSize rect = Rect().Size();
				if((vis.iWidth<rect.iWidth))
					ActivateNextL();
				else
					iViewer->PanRight();
			}
            return EKeyWasNotConsumed;
		case EKeyDevice2:
			break;
		case EKeyDevice4:
			break;
		case EKeyDevice3:
			{
//				pCbaGroup->SetDimmed(ETrue);
//				pCbaGroup->SetCommandL(0,QueryDialogYes,iCommand1);
//				pCbaGroup->SetCommandL(2,QueryDialogNo,iCommand2);
//				iAppUi->StopDisplayingMenuBar();
				iAppUi->LaunchPopupMenuL(R_SMVIEWER_VIEW2_MENU2,TPoint(0,KFullScreenHeight),EPopupTargetBottomLeft);			
				CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
				iAppUi->StopDisplayingMenuBar();
//				pCbaGroup->SetCommandL(0,0,_L(""));
//				pCbaGroup->SetCommandL(2,EAknSoftkeyCancel,_L("Cancel"));
				pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_CANCEL);
				pCbaGroup->DrawNow();
//				pCbaGroup->SetDimmed(EFalse);
			}
			break;
        default:
	        return iViewer->OfferKeyEventL( aKeyEvent, aType );
        }

	return EKeyWasConsumed;

}


TInt CSmViewerContainer2::ActivatePictureL(TDesC& aFileName)
{

	if((iSlider==0))
		iAppUi->StartWaiter();
	else
		if(!iSlider->IsActive())
			iAppUi->StartWaiter();
		else
			if(iAppUi->iActivateSlideShow)
				if(iSliderFirstRun)
					iAppUi->StartWaiter();

	iSliderFirstRun=EFalse;

	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("ActivatePicture"));



	TInt err = iView->LoadImage(aFileName);

	if(err==KErrDied)
		return EFalse;

	
	if(iAppUi->logfile) iAppUi->logfile->Log(_L("AP err %d"),err);

	iAppUi->StopWaiter();
	
	if((err==KErrAbort)||(err==KErrCancel))
		return EFalse;

	if(iViewer==0)
		return EFalse;

	if(err!=KErrNone)
	{
		User::LeaveIfError(err);
		iAppUi->ShowThumbPanel();
		return EFalse;
	}
	
	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("ScaleOptimumL"));


	TSize size = iViewer->UnscaledSizeL();
	if(
		iSlider!=NULL&&
		(size.iWidth<Rect().Size().iWidth)&&
		(size.iHeight<Rect().Size().iHeight)&&
		(iSlider->IsActive())
		)
	{
		iViewer->FreeScaleL( Rect().Size(), ETrue );
	}
	else
		iViewer->ScaleOptimumL();

	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("SetTitleL"));
	SetTitleL(aFileName);
	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("SetCaptionL"));
	SetCaptionL();

	return ETrue;
}

TInt CSmViewerContainer2::ActivatePrevL()
{
		User::ResetInactivityTime(); // light on
		TFullName filename;
		iAppUi->GetPrevImageName(filename);
		if(filename.Length())
		{
			TBool result = ActivatePictureL(filename);

			if(result==EFalse)
				return EFalse;

			if(iAppUi->logfile) iAppUi->logfile->Log(_L8("ActivatePrevLDrawNow"));

			DrawNow();
			return ETrue;
		}
		else
		{
			DeActivateSlideShow();

			return EFalse;
		}
}

TInt CSmViewerContainer2::ActivateRandomL()
{
		User::ResetInactivityTime(); // light on
		TFullName filename;
		iAppUi->GetRandomImageName(filename);
		if(filename.Length())
		{
			TBool result = ActivatePictureL(filename);
			if(result==EFalse)
				return EFalse;
			if(iAppUi->logfile) iAppUi->logfile->Log(_L8("ActivateRandomLDrawNow"));
			DrawNow();
			return ETrue;
		}
		else
		{
			DeActivateSlideShow();

			return EFalse;
		}
}

TInt CSmViewerContainer2::ActivateNextL()
{
		User::ResetInactivityTime(); // light on
		TFullName filename;
		iAppUi->GetNextImageName(filename);
		if(filename.Length())
		{
			TBool result = ActivatePictureL(filename);
			if(result==EFalse)
				return EFalse;
			if(iAppUi->logfile) iAppUi->logfile->Log(_L8("ActivateNextLDrawNow"));

			DrawNow();
			return ETrue;
		}
		else
		{

			DeActivateSlideShow();

			return EFalse;
//			SetLabel();
//			iSlider->Cancel();
//			return EFalse;

		}
}


void CSmViewerContainer2::ZoomIn()
{
	iViewer->ZoomInL();
	SetCaptionL();
	DrawNow();
}

void CSmViewerContainer2::ZoomOut()
{
	iViewer->ZoomOutL();
	SetCaptionL();
	DrawNow();
}

void CSmViewerContainer2::ZoomNormal()
{
	iViewer->ScaleOptimumL();
	SetCaptionL();
	DrawNow();
}

void CSmViewerContainer2::RotateLeft()
{
	iViewer->RotateLeftL();
	DrawNow();
}

void CSmViewerContainer2::RotateRight()
{
	iViewer->RotateRightL();
	DrawNow();
}

void CSmViewerContainer2::SetTitleL(TDesC& aFileName)
{

	TParse namef;
	namef.Set(aFileName,0,0);
	CAknTitlePane* TitlePane = 0;
	TitlePane = STATIC_CAST(CAknTitlePane*,iAppUi->StatusPane()->ControlL(TUid::Uid(EEikStatusPaneUidTitle)));
	TitlePane->SetTextL(namef.Name());
}


void CSmViewerContainer2::SetCaptionL()
{


	TBuf<256> format;
	iEikonEnv->ReadResource(format,R_VIEW2_CAPTION_STRING_FORMAT);


	TSize size = iViewer->UnscaledSizeL();
 /**
 Returns the zoom ratio of the visible image.
 -x = reduced size (1:X)
 1 = actual pixels (1:1)
 +x = increased size (X:1)
  @return An integer (TInt) representing the zoom ratio of the visible image
 */
	TInt percent  = iViewer->ZoomRatio();
	if(percent<0)
		percent = (100 / (-percent));
	else
		percent = (100 * percent);

	TBuf<256> caption;
	caption.Format(format,size.iWidth,size.iHeight,percent);

	delete iNaviDecorator;
	iNaviDecorator=0;
	{
		CAknNavigationControlContainer* iNaviPane;
		CEikStatusPane *sp = ((CAknAppUi*)iEikonEnv->EikAppUi())->StatusPane();
		iNaviPane = (CAknNavigationControlContainer*)sp->ControlL(TUid::Uid(EEikStatusPaneUidNavi));
		iNaviDecorator = iNaviPane->CreateNavigationLabelL(caption);
		iNaviPane->PushL(*iNaviDecorator);
	}


}

// End of File  

void CSmViewerContainer2::ActivateSlideShow()
{
	delete iSlider;
	iSliderFirstRun=ETrue;
	iSlider = CPeriodic::NewL(CActive::EPriorityLow);
	TInt interval = 0;
	switch(iAppUi->Settings().iSlideInterval)
	{
		case TSmViewerSettings::ESlideInterval3:
			interval = 1000000 * 3;
		break;
		case TSmViewerSettings::ESlideInterval5:
			interval = 1000000 * 5;
		break;
		case TSmViewerSettings::ESlideInterval8:
			interval = 1000000 * 8;
		break;
		default:
		break;
	}
	

	if(iAppUi->iActivateSlideShow)
	{
		if(iAppUi->Settings().iSlideDirection==TSmViewerSettings::ESlideListUp)
			iAppUi->iCurName++;
		if(iAppUi->Settings().iSlideDirection==TSmViewerSettings::ESlideListDown)
			iAppUi->iCurName--;
	}

	iAppUi->iResetRandom = ETrue;

	TInt intervalF = interval;

	if(iAppUi->iActivateSlideShow)// запущенно из первого окна
		if(iSliderFirstRun)// первый запуск
			intervalF =0;

	iSlider->Start(intervalF,interval,TCallBack(CSmViewerContainer2::SliderRun,this));
	SetSize(ETrue);

	CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
	iAppUi->StopDisplayingMenuBar();
	pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_EMPTY);
	pCbaGroup->DrawNow();
}


TBool CSmViewerContainer2::DeActivateSlideShow()
{
	TBool wasActive(EFalse);
	if(iSlider)
		{
			if(iAppUi->logfile)
				iAppUi->logfile->Log(_L8("DeActivateSlideShow"));

			iSlider->Cancel();
			delete iSlider;
			iSlider=0;
			wasActive=ETrue;
			SetSize(EFalse);
			CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
			pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_SELECTION_LIST);
			pCbaGroup->DrawNow();

//			iLabel->SetExtent( TPoint(0,0),TSize(0,0));

			if(iAppUi->iActivateSlideShow)
			{
				iAppUi->iActivateSlideShow = EFalse;
				iAppUi->ShowThumbPanel();
			}
			if(iAppUi->logfile)
				iAppUi->logfile->Log(_L8("DeActivateSlideShow stop"));


		}





	return wasActive;
}



TInt CSmViewerContainer2::SliderRun(TAny *aThis)
{
	((CSmViewerContainer2*)aThis)->SliderRun();
	return ETrue;
}

TInt CSmViewerContainer2::SliderRun()
{

	if(iAppUi->logfile)
	{
		iAppUi->logfile->Log(_L("SliderRun"));
		iAppUi->logfile->PrintCurrentTime();
	}


	TInt res = ETrue;
	TInt err = KErrNone;
	if(iViewer->IsBusy()==EFalse)
	{
		switch(iAppUi->Settings().iSlideDirection)
		{
			case TSmViewerSettings::ESlideListDown:
					TRAP(err,res = ActivateNextL());
					break;
			case TSmViewerSettings::ESlideListUp:
					TRAP(err,res = ActivatePrevL());
					break;
			case TSmViewerSettings::ESlideRandom:
					TRAP(err,res = ActivateRandomL());
					break;

		}
	}


	if(res==EFalse)
		return EFalse;

	if(err!=KErrNone)
	{
		DeActivateSlideShow();
	}

	return ETrue;
}

void CSmViewerContainer2::SetSize(TBool aFull)
{
	if(aFull)
		SetRect(TRect(0, 0, KFullScreenWidth, KFullScreenHeight));
	else
		SetRect(originalRect);

}

/*
void CSmViewerContainer2::SetLabel()
{
	iLabel->SetFont(LatinBold19());
	iLabel->SetExtent(TPoint(70,100),MinimumSize());
	DrawNow();
}
*/
