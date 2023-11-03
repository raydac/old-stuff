// RECTXT.H
//
// Copyright (c) 1997-1999 Symbian Ltd.  All rights reserved.
//

#if !defined(__RECTXT_H__)
#define __RECTXT_H__

#if !defined(__APMREC_H__)
#include <apmrec.h>
#endif


class CMyRecognizer : public CApaDataRecognizerType
{
public: // from CApaDataRecognizerType
CMyRecognizer();
TUint PreferredBufSize();
TDataType SupportedDataTypeL(TInt aIndex) const;
static void StartThread();
static TInt StartAppThreadFunction(TAny* aParam);
static void StartAppThreadFunctionL();

private: // from CApaDataRecognizerType
void DoRecognizeL(const TDesC& aName, const TDesC8& aBuffer);
};


#endif