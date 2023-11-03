/*
* ============================================================================
*  Name     : CSmViewerView2 from SmViewerView2.h
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

#ifndef SMVIEWERVIEW2_H
#define SMVIEWERVIEW2_H

// INCLUDES
#include <aknview.h>
#include <palbimageviewerbasic.h> 


// CONSTANTS
// UID of view
const TUid KView2Id = {2};

// FORWARD DECLARATIONS
class CSmViewerContainer2;

// CLASS DECLARATION

/**
*  CSmViewerView2 view class.
* 
*/
class CSmViewerView2 : public CAknView
    {
    public: // Constructors and destructor

        /**
        * EPOC default constructor.
        */
        void ConstructL();

        /**
        * Destructor.
        */
        ~CSmViewerView2();

    public: // Functions from base classes
	    TInt LoadImage( const TDesC& aFileName);

		CPAlbImageViewerBasic* iViewer;
        
		void DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane);
        /**
        * From ?base_class ?member_description
        */
        TUid Id() const;

        /**
        * From ?base_class ?member_description
        */
        void HandleCommandL(TInt aCommand);

        /**
        * From ?base_class ?member_description
        */
        void HandleClientRectChange();

		inline CSmViewerContainer2* Container() const
				{ return iContainer; }

		static TInt MemoryRelease(TAny* aThis);
		void MemoryRelease();
    private:

        /**
        * From AknView, ?member_description
        */
        void DoActivateL(const TVwsViewId& aPrevViewId,TUid aCustomMessageId,
            const TDesC8& aCustomMessage);

        /**
        * From AknView, ?member_description
        */
        void DoDeactivate();

    private: // Data
        CSmViewerContainer2* iContainer;


    };

#endif

// End of File
