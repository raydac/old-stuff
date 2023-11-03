/*


#ifndef __IMFILEFINDER_H__
#define __IMFILEFINDER_H__

#include <e32base.h>
#include <aknlists.h>
#include "SmViewerContainer.h"

class CImFileFinder : public CBase
{
public:

    static CImFileFinder* NewL();
    static CImFileFinder* NewLC();
    ~CImFileFinder();

private:
    
    CImFileFinder();

	void ForAllMatchingFiles(const TDesC& aScanDir);
	static void FindImages(TAny* aThis);
	void FindImages();
	void StartFindImages();

//	void StartFinder(CAknColumnListBox* aListBox,CSmViewerContainer* iObserver);


    void ConstructL();

private:
	TFullName iScanDir;
	CAknColumnListBox* iListBox;
	CSmViewerContainer* iObserver;
	TRequestStatus iRunLProgress;
	CIdle* iIdle;
};

#endif // __IMFILEFINDER_H__

*/
