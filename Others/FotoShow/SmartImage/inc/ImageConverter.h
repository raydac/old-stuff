/* Copyright (c) 2003, Nokia Mobile Phones. All rights reserved */

#ifndef __IMAGECONVERTER_H__
#define __IMAGECONVERTER_H__

#include <e32base.h>
#include <mdaimageconverter.h>

class CSmartImageAppUi;

class CImageConverter : public CBase, MMdaImageUtilObserver
{
public:

	enum TLoadStatus
	{
		EStopLStatus,
		EOpeningLStatus,
		EConvertingLStatus,
		EScalingLStatus,
		ECompleteLStatus
	};


	void StartFileToBitmapConvert(const TDesC& aFileName);
	void StartDescriptorToBitmapConvert(const TDesC8& aImageData);

/*!
  @function NewL
   
  @discussion Create a CImageConverter object
  @result a pointer to the created instance of CImageConverter
  */
    static CImageConverter* NewL(CSmartImageAppUi* aAppUi);

/*!
  @function NewLC
   
  @discussion Create a CImageConverter object
  @result a pointer to the created instance of CImageConverter
  */
    static CImageConverter* NewLC(CSmartImageAppUi* aAppUi);

/*!
  @function ~CImageConverter
  
  @discussion Destroy the object and release all memory objects
  */
    ~CImageConverter();

private:


// MMdaImageUtilObserver
	void MiuoCreateComplete(TInt aError);
	void MiuoOpenComplete(TInt aError);
	void MiuoConvertComplete(TInt aError);


/*!
  @function CImageConverter

  @discussion Constructs this object
  */
    CImageConverter(CSmartImageAppUi* aAppUi);

/*!
  @function ConstructL

  @discussion Performs second phase construction of this object
  */
    void ConstructL();

private:
    // Member variables
	CMdaImageDataReadUtility* iConverter;
	CMdaBitmapScaler* iScaler;

	TBool iConverting;
	TBool iScaling;

	CSmartImageAppUi* iAppUi;
};

#endif // __IMAGECONVERTER_H__

