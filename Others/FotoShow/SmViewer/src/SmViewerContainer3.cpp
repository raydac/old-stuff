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

#include "SmViewerContainer3.h"
#include <barsread.h>
#include <SmViewer.rsg>
#include <aknview.h>
#include <akntitle.h>
#include <gulicon.h>
#include <eikaufty.h>
#include <eikmenup.h>
#include <smviewer.mbg>
#include <eikimage.h>
#include "SmViewerContainer.h"

const TInt KFullScreenHeight = 188;
const TInt KHeaderSize = 1024;
const TInt KBytesPerPixel = 3;

_LIT(KSmViewerMbmFileName, "\\system\\apps\\SMVIEWER\\SMVIEWER.MBM");

CSmViewerContainer3::~CSmViewerContainer3()
{
	if(iContextPane)
	{
		CFbsBitmap* bmpnew = iEikonEnv->CreateBitmapL(KSmViewerMbmFileName, EMbmSmviewerNoimage);
		CEikImage* img = new (ELeave) CEikImage();
		img->SetPicture(bmpnew);
		CEikImage* old = iContextPane->SwapPicture(img);
		iContextPane->SetPictureToDefaultL();
		CFbsBitmap* bmpnew2 = iEikonEnv->CreateBitmapL(KSmViewerMbmFileName, EMbmSmviewerNoimage);
		old->SetBitmap(bmpnew2);
		delete old;
	}
	delete iNaviDecorator;

	
	if(iTextArray)
		for(TInt i=0;i<iTextArray->Count();i++)
		{
			if((*iTextArray)[i].iThumbail!=0)
			{
				delete (*iTextArray)[i].iThumbail;
				(*iTextArray)[i].iThumbail =0;
			}
		}

	iListBox->ItemDrawer()->ColumnData()->SetIconArray(0);
	delete iListBox;


	delete iSaver;

	delete iLoader;
}


void CSmViewerThunbLoader::RunL()
{
	while(++iCurrentThumbail<iCont->iTextArray->Count())
	{
		if(iCont->iTextArray->At(iCurrentThumbail).iThumbail!=0)
		{

#ifdef _DEBUG
			TContactInfo info = iCont->iTextArray->At(iCurrentThumbail);
#endif

//			delete iConverter;
//			iConverter = NULL;
//			iConverter = CMdaImageDescToBitmapUtility::NewL(*this);
			((CMdaImageDescToBitmapUtility*)iConverter)->OpenL(*(iCont->iTextArray->At(iCurrentThumbail).iThumbail)); 
			break;
		}
	}
}

void CSmViewerThunbLoader::DoCancel()
{
	if(iConverter)
		iConverter->CancelConvertL();
	delete iConverter;
	iConverter = NULL;
}

CSmViewerContainer3::CSmViewerContainer3() 
{

}

void CSmViewerContainer3::ConstructL(const TRect& aRect,CSmViewerAppUi* aAppUi)
{
	iAppUi = aAppUi;
    CreateWindowL();
	iSaver=0;

	TFullName flname;
	
	iAppUi->GetCurImageName(flname);
	flname.LowerCase();
	iImgInfo.iFileName.Set(flname,0,0);
	iAppUi->UpdateImageNumber(iImgInfo);

    iListBox = new (ELeave) CAknDoubleLargeStyleListBox;
    iListBox->SetContainerWindowL( *this );
	iListBox->SetListBoxObserver(this);

	TResourceReader reader;
    CEikonEnv::Static()->CreateResourceReaderLC( reader,R_LIST_SINGLE_LARGE_GRAPHIC);
    iListBox->ConstructFromResourceL( reader );
    CleanupStack::PopAndDestroy(); // resource stuffs.

    SetRect(aRect);

	iTextArray = new (ELeave) CListContactsArray(5);
	iListBox->Model()->SetItemTextArray(iTextArray);
	iListBox->ItemDrawer()->ColumnData()->SetIconArray(iAppUi->View1IconsArray());
	CreateScrollbarL();

	iAppUi->DbTools()->UpadteContactsL(iTextArray);
	for(TInt i=0;i<iTextArray->Count();i++)
	{
		if(iTextArray->At(i).iFileName.FullName().Length()!=0)
			iAppUi->UpdateImageNumber((*iTextArray)[i]);

		CreateText((*iTextArray)[i]);
	}

	iListBox->HandleItemAdditionL();
	iListBox->SetCurrentItemIndex(0);

	iNaviDecorator=0;


	Sorting();
	SetCaptionL();

    ActivateL();

	iLoader = new (ELeave) CSmViewerThunbLoader(this);
	iLoader->Start();
}

void CSmViewerContainer3::CreateText(TContactInfo& aInfo)
{
		TBuf<255> string;
		if(aInfo.iImageIndex!=0)
			string.Format(_L("%d"),aInfo.iImageIndex);
		else
			string.Format(_L("%d"),aInfo.iAltImageIndex);

		string.Append(_L("\t"));
		string.Append(aInfo.iTelephone);
		string.Append(_L("\t"));
		string.Append(aInfo.iContactName);
/*
		if(aInfo.iLinked == EFalse)
			string.Append(_L("\t1"));
		else
			string.Append((_L("\t2")));
*/

		aInfo.iText = string;
}


void CSmViewerContainer3::HandleListBoxEventL(CEikListBox* /*aListBox*/, TListBoxEvent /*aEventType*/)
{
}


void CSmViewerContainer3::CreateScrollbarL()
    {
    if ( iListBox )
        {
        // Creates scrollbar.
        iListBox->CreateScrollBarFrameL( ETrue );
        iListBox->ScrollBarFrame()->SetScrollBarVisibilityL(
            CEikScrollBarFrame::EOff, CEikScrollBarFrame::EAuto );
        }
    }

TInt CSmViewerContainer3::CountComponentControls() const
    {
    return 1;
    }

void CSmViewerContainer3::Draw(const TRect& /*aRect*/) const
    {    
/*
    CWindowGc& gc = SystemGc();
    gc.SetPenStyle(CGraphicsContext::ENullPen);
    gc.SetBrushColor(KRgbBlue);
    gc.SetBrushStyle(CGraphicsContext::ESolidBrush);
    gc.DrawRect(aRect);*/

    }

CCoeControl* CSmViewerContainer3::ComponentControl(TInt aIndex) const
    {
    switch ( aIndex )
        {
        case 0:
            return iListBox;
        default:
            return NULL;
        }
    }

TKeyResponse  CSmViewerContainer3::OfferKeyEventL(const TKeyEvent& aKeyEvent,TEventCode aType )
{
    if ( aType != EEventKey ) // Is not key event?
        {
        return EKeyWasNotConsumed;
        }

    if ( iListBox )
		if ( iTextArray->Count()>0 )
        {
		  switch ( aKeyEvent.iCode )
			{
			case EKeyLeftArrow:
				return EKeyWasConsumed;
			case EKeyRightArrow:
				return EKeyWasNotConsumed;
			case EKeyDevice3:
				{
				iAppUi->LaunchPopupMenuL(R_SMVIEWER_VIEW3_MENU,TPoint(0,KFullScreenHeight),EPopupTargetBottomLeft);			
				CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
				iAppUi->StopDisplayingMenuBar();
				pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_CANCEL);
				pCbaGroup->DrawNow();
				}
				return EKeyWasConsumed;
			default:
				{
					TKeyResponse key;
					key = iListBox->OfferKeyEventL( aKeyEvent, aType );
					return key;
				}
			}
       }

	return EKeyWasNotConsumed;

}



void CSmViewerContainer3::SizeChanged()
    {
    // TODO: Add here control resize code etc.
    iListBox->SetExtent( TPoint(0,0), Rect().Size());
    }








CListContactsArray::CListContactsArray(TInt aGranularity) 
: CArrayFixFlat<TContactInfo>(aGranularity)
{
	
}


EXPORT_C TInt  CListContactsArray::MdcaCount() const
{
	return 	Count();
}


EXPORT_C TPtrC16 CListContactsArray::MdcaPoint(TInt aIndex) const
{
	return At(aIndex).iText;
}


TInt CSmViewerContainer3::UpdateImages(CListContactsArray *aArray)
{
	for(TInt i=0;i<aArray->Count();i++)
	{
		iAppUi->UpdateImageNumber((*aArray)[i]);
	}

	return 0;		
}


void CSmViewerContainer3::SetCaptionL()
{

	TBuf<128> caption;
	iEikonEnv->ReadResource(caption,R_VIEW3_CAPTION_STRING);

	CAknTitlePane* TitlePane = 0;
	TitlePane = STATIC_CAST(CAknTitlePane*,iAppUi->StatusPane()->ControlL(TUid::Uid(EEikStatusPaneUidTitle)));
	TitlePane->SetTextL(caption);

	CEikStatusPane *sp = ((CAknAppUi*)iEikonEnv->EikAppUi())->StatusPane();


	TBuf<1> caption2;
	
	delete iNaviDecorator;
	{
		CAknNavigationControlContainer* iNaviPane;
		iNaviPane = (CAknNavigationControlContainer*)sp->ControlL(TUid::Uid(EEikStatusPaneUidNavi));
		iNaviDecorator = iNaviPane->CreateNavigationLabelL(caption2);
		iNaviPane->PushL(*iNaviDecorator);
	}


	iContextPane = 0;
    iContextPane = (CAknContextPane *)sp->ControlL(TUid::Uid(EEikStatusPaneUidContext));

//	CFbsBitmap* emptybmp = 
//	img->c

	CFbsBitmap* bmp = iAppUi->View1IconsArray()->At(iImgInfo.iImageIndex)->Bitmap();
    iContextPane->SetPicture(bmp);
	bmp=0;
}


void CSmViewerContainer3::AttachL()
{
	TInt index = iListBox->CurrentItemIndex();
	oldImageIndex = (*iTextArray)[index].iImageIndex;
	(*iTextArray)[index].iImageIndex = iImgInfo.iImageIndex;
	(*iTextArray)[index].iAltImageIndex = iImgInfo.iImageIndex;
	(*iTextArray)[index].iFileName = iImgInfo.iFileName;
	(*iTextArray)[index].iLinked = ETrue;
	CreateText((*iTextArray)[index]);
	DrawNow();

	delete iSaver;

	if(iTextArray->At(index).iAltImageIndex!=0)
	{
//#ifdef _DEBUG
//			TContactInfo info = iCont->iTextArray->At(iCurrentThumbail);
//#endif

		TContactInfo& inf = ((*(iTextArray))[index]);
		delete inf.iThumbail;
		inf.iThumbail=NULL;
		CAknIconArray* icons = iAppUi->View1IconsArray();

		const TSize size = icons->At(inf.iImageIndex)->Bitmap()->SizeInPixels();
		if (size.iWidth>0 && size.iHeight>0)
		{
			TInt descSize = (size.iWidth * size.iHeight * KBytesPerPixel) + KHeaderSize;   //why these magic numbers????
//					TInt descSize = 200000; // for testing
			TRAPD(err, inf.iThumbail = HBufC8::NewL(descSize));
			iSaver = new (ELeave) CSmViewerThunbSaver(iAppUi,icons->At(inf.iImageIndex)->Bitmap(),inf.iThumbail,this);
			iSaver->Start();
		}
	}
	else
		AttachSecondPhaseL();


	
}

void CSmViewerContainer3::AttachSecondPhaseL()
{
	TInt index = iListBox->CurrentItemIndex();
	iAppUi->DbTools()->UpdateContactL((*iTextArray)[index]);
	iAppUi->UpdateImageLink((*iTextArray)[index].iImageIndex);
	iAppUi->UpdateImageLink(oldImageIndex);
}



void CSmViewerContainer3::DeAttachL()
{
	TInt index = iListBox->CurrentItemIndex();
	TInt imgindex = (*iTextArray)[index].iImageIndex;
//	if(iTextArray->At(index).iImageIndex!=0)
	{
		(*iTextArray)[index].iImageIndex = 0;
		(*iTextArray)[index].iAltImageIndex = 0;
		(*iTextArray)[index].iFileName.Set(_L(""),0,0);
		(*iTextArray)[index].iLinked = EFalse;
		(*iTextArray)[index].iAltImageIndex = 0;
	}
	CreateText((*iTextArray)[index]);
	DrawNow();

	iAppUi->DbTools()->UpdateContactL((*iTextArray)[index]);
	iAppUi->UpdateImageLink(imgindex);
	// save information
}


void CSmViewerThunbLoader::MiuoCreateComplete(TInt /*aError*/)
	{
	}

void CSmViewerThunbLoader::MiuoOpenComplete(TInt aError)
{
	if (aError==KErrNone)
		{	
		TFrameInfo info;
		iConverter->FrameInfo(0,info);
		CFbsBitmap* bitmap=NULL;
		TRAPD(err,bitmap = new (ELeave) CFbsBitmap());
		if (err==KErrNone)
			{
			bitmap->Create(info.iOverallSizeInPixels, EColor4K);
			CAknIconArray* icons = iCont->iAppUi->View1IconsArray();
			CGulIcon* icon = CGulIcon::NewL(bitmap);
			icons->AppendL(icon);
			(*iCont->iTextArray)[iCurrentThumbail].iAltImageIndex = icons->Count()-1;
			(*iCont->iTextArray)[iCurrentThumbail].iLinked = ETrue;
			TRAPD(error,iConverter->ConvertL(*bitmap));
			iConverting = ETrue;
			iScaling = EFalse;
			}
		}

}

void CSmViewerThunbLoader::MiuoConvertComplete(TInt aError)
{

	if(iScaling)
	{
		iScaling = EFalse;
	}

	if(iConverting)
		{
		if (aError==KErrNone)
		{
//			CFbsBitmap* bitmap=(CFbsBitmap*)iAppContainer->Image()->Bitmap();
//			TSize bitmapSize = bitmap->SizeInPixels();

//			if(bitmapSize.iWidth > iEikonEnv->EikAppUi()->ClientRect().Width() || bitmapSize.iHeight > iEikonEnv->EikAppUi()->ClientRect().Height())
//			{
//				delete iScaler;
//				iScaler = NULL;
//				TRAPD(err, iScaler = CMdaBitmapScaler::NewL());
//				if (err==KErrNone)
//					{
//					TRAPD(error, iScaler->ScaleL(*this, *bitmap, iEikonEnv->EikAppUi()->ClientRect().Size())); // maintains aspect ratio
//					if (error==KErrNone)
//						{
//						iScaling = ETrue;
//						}
//					}
//			}
//			else// if(bitmapSize.iWidth > iEikonEnv->EikAppUi()->ClientRect().Width() || bitmapSize.iHeight > iEikonEnv->EikAppUi()->ClientRect().Height())
			{
				iScaling = EFalse;

			}
		}//		if (aError==KErrNone)
		iConverting=EFalse;

		iCont->CreateText((*iCont->iTextArray)[iCurrentThumbail]);
		iCont->DrawNow();
		delete (*iCont->iTextArray)[iCurrentThumbail].iThumbail;
		(*iCont->iTextArray)[iCurrentThumbail].iThumbail =0;
		Start();
	}

}


CSmViewerThunbLoader::CSmViewerThunbLoader(CSmViewerContainer3* aCont): CActive(CActive::EPriorityStandard)
{
	iCont = aCont;
	iCurrentThumbail=-1;
	iConverting = EFalse;
	iConverter = 0;
	iScaler = 0;
	iScaling = EFalse;


	CActiveScheduler::Add(this);
	iConverterType =0;
	iConverter = CMdaImageDescToBitmapUtility::NewL(*this);
}

CSmViewerThunbLoader::~CSmViewerThunbLoader()
{
	Cancel();
	delete iConverter;
	delete iScaler;
}


void CSmViewerThunbLoader::Start()
//
// Start the activator.
//
{
	TRequestStatus* pS=(&iStatus);
	User::RequestComplete(pS,0);
	SetActive();
}


void CSmViewerThunbSaver::MiuoCreateComplete(TInt aError)
{

	if (aError==KErrNone)
		{
		iConverting=ETrue;
		TRAPD(err,iConverter->ConvertL(*iBitmap));
		}
	else
		{
		iConverting=EFalse;
		}

}

void CSmViewerThunbSaver::MiuoOpenComplete(TInt aError)
{
	if (aError==KErrNone)
		{	
	}

}

void CSmViewerThunbSaver::MiuoConvertComplete(TInt aError)
{

	if(iConverting)
		{
		if (aError==KErrNone)
		{
			iObserver->AttachSecondPhaseL();
		}
		iConverting=EFalse;
	}
}


CSmViewerThunbSaver::CSmViewerThunbSaver(CSmViewerAppUi* aAU,CFbsBitmap* aBitmap,HBufC8* aBuffer,MSmViewerThunbSaverObserver* aObserver)
: CActive(CActive::EPriorityStandard)
{
	iUI = aAU;
	iBitmap = aBitmap;
	iCurrentThumbail=-1;
	iConverting = EFalse;
	iConverter = 0;
	iBuffer = aBuffer;

	iObserver = aObserver;

	CActiveScheduler::Add(this);
	iConverterType =0;
	iConverter = CMdaImageBitmapToDescUtility::NewL(*this);
}

CSmViewerThunbSaver::~CSmViewerThunbSaver()
{
//	delete iBuffer;
	delete iPtr;
	Cancel();
	delete iConverter;
}


void CSmViewerThunbSaver::Start()
//
// Start the activator.
//
{
	TRequestStatus* pS=(&iStatus);
	User::RequestComplete(pS,0);
	SetActive();
}


void CSmViewerThunbSaver::RunL()
{

	if ( iBitmap != NULL ) 
		{
			{
				{
				delete iPtr;
				iPtr = NULL;
				iPtr = new(ELeave) TPtr8(iBuffer->Des());
				jfifFormat.iSettings.iQualityFactor = 100;
				TRAPD(err,
					{
					iConverter->CreateL(*iPtr, &jfifFormat, NULL, NULL);
					}
				);
				}
			}
		}

}

void CSmViewerThunbSaver::DoCancel()
{
	if(iConverter)
		iConverter->CancelConvertL();
	delete iConverter;
	iConverter = NULL;
}

void CSmViewerContainer3::Sorting()
{

//	if(sort == TSmViewerSettings::EByName)
	{
		TKeyArrayFix actNameKey(_FOFF(TContactInfo,iContactName),ECmpNormal);
		iTextArray->Sort(actNameKey);
	}

	if(iListBox)
		iListBox->HandleItemAdditionL();

}
