/* Copyright (c) 2003, Nokia Mobile Phones. All rights reserved */

#include "ImageConverter.h"
#include "SmartImageApp.h"
#include "SmartImageAppUi.h"
#include "SmartImageContainer2.h"
#include <eikimage.h>


CImageConverter* CImageConverter::NewL(CSmartImageAppUi* aAppUi)
    {
    CImageConverter* self = NewLC(aAppUi);
    CleanupStack::Pop(self);
    return self;
    }

    
CImageConverter* CImageConverter::NewLC(CSmartImageAppUi* aAppUi)
{
    CImageConverter* self = new (ELeave) CImageConverter(aAppUi);
    CleanupStack::PushL(self);
    self->ConstructL();
    return self;
}


CImageConverter::CImageConverter(CSmartImageAppUi* aAppUi)
{
	iConverter =0;
	iScaler =0;
	iAppUi = aAppUi;
	iConverting = EFalse;
	iScaling=EFalse;
}


CImageConverter::~CImageConverter()
{
	if(iConverter)
		iConverter->CancelConvertL();
	delete iConverter;
	if(iScaler)
		iScaler->CancelScaling();
	delete iScaler;
}


void CImageConverter::ConstructL()
    {
    }



void CImageConverter::StartFileToBitmapConvert(const TDesC &aFileName)
{
	delete iConverter;
	iConverter = NULL;
	iAppUi->infoAboutCurrentLoadState.iStatus = EOpeningLStatus;
	iConverter = CMdaImageFileToBitmapUtility::NewL(*this);
	((CMdaImageFileToBitmapUtility*)iConverter)->OpenL(aFileName); 
}

void CImageConverter::StartDescriptorToBitmapConvert(const TDesC8 &aImageData)
{
	delete iConverter;
	iConverter = NULL;
	iConverter = CMdaImageDescToBitmapUtility::NewL(*this);
	iAppUi->infoAboutCurrentLoadState.iStatus = EOpeningLStatus;
	((CMdaImageDescToBitmapUtility*)iConverter)->OpenL(aImageData); 
}


void CImageConverter::MiuoCreateComplete(TInt /*aError*/)
{
// should not happen
}

void CImageConverter::MiuoOpenComplete(TInt aError)
{
	if(iAppUi->iInterceptor->logfile) 
	iAppUi->iInterceptor->logfile->Log(_L8("MiuoOpenComplete"));

	if (aError==KErrNone)
		{	
		TFrameInfo info;
		iConverter->FrameInfo(0,info);
		CFbsBitmap* bitmap=NULL;
		TRAPD(err,bitmap = new (ELeave) CFbsBitmap());
		if (err==KErrNone)
			{
			bitmap->Create(info.iOverallSizeInPixels, EColor4K);
			iAppUi->ShowContainerL()->Image()->SetPicture(bitmap);// CEikImage* iImage;
			iAppUi->infoAboutCurrentLoadState.iStatus = EConvertingLStatus;
			iAppUi->ShowContainerL()->DrawNow();
			TRAPD(error,iConverter->ConvertL(*bitmap));
			iConverting = ETrue;
			iScaling = EFalse;

			}
		}

if(iAppUi->iInterceptor->logfile) 
	iAppUi->iInterceptor->logfile->Log(_L8("MOC end"));

}

void CImageConverter::MiuoConvertComplete(TInt aError)
{

	if(iAppUi->iInterceptor->logfile) 
	iAppUi->iInterceptor->logfile->Log(_L8("MiuoConvertComplete"));

	if(iScaling)
	{
		iScaling = EFalse;
		iAppUi->infoAboutCurrentLoadState.iStatus = ECompleteLStatus;
		iAppUi->ShowContainerL()->DrawNow();
	}

	if(iConverting)
		{

		if (aError==KErrNone)
		{
			CFbsBitmap* bitmap=(CFbsBitmap*)(iAppUi->ShowContainerL()->Image()->Bitmap());
			TSize bitmapSize = bitmap->SizeInPixels();

			if(bitmapSize.iWidth > iAppUi->ShowContainerL()->Rect().Width() || bitmapSize.iHeight > iAppUi->ShowContainerL()->Rect().Height())
			{
				iAppUi->infoAboutCurrentLoadState.iStatus = EScalingLStatus;
				iAppUi->ShowContainerL()->DrawNow();
				delete iScaler;
				iScaler = NULL;
				TRAPD(err, iScaler = CMdaBitmapScaler::NewL());
				if (err==KErrNone)
					{
					TRAPD(error, iScaler->ScaleL(*this, *bitmap, iAppUi->ShowContainerL()->Rect().Size())); // maintains aspect ratio
					if (error==KErrNone)
						{
						iScaling = ETrue;
						}
					}
			}
			else// if(bitmapSize.iWidth > iEikonEnv->EikAppUi()->ClientRect().Width() || bitmapSize.iHeight > iEikonEnv->EikAppUi()->ClientRect().Height())
			{
				iScaling = EFalse;
				iAppUi->infoAboutCurrentLoadState.iStatus = ECompleteLStatus;
				iAppUi->ShowContainerL()->DrawNow();

			}
		}//		if (aError==KErrNone)


		iConverting=EFalse;
	}

	if(iAppUi->iInterceptor->logfile) 
	iAppUi->iInterceptor->logfile->Log(_L8("MCC end"));

}
