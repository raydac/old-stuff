/* ========================================================================
  * Author:  Oleg Golosovskiy, http://www.sw4u.org mailto:mail@sw4u.org
  * ======================================================================== */
/** 
  * \author Oleg Golosovsky     
  * \brief	Loger 
  */

#ifndef __PHONEBOOKTOOLS_H__
#define __PHONEBOOKTOOLS_H__

#include <e32base.h>
#include <cntdb.h>
struct TContactInfo;
class CSmViewerContainer;

/// Forward declarartion
class CListContactsArray;


/**
	Class for work with contact database. Consist functions for notifier functioning
*/
class CPhoneBookTools : public CBase
{
public:

/**
	Update contacts info
	 \param TContactInfo& iInfo - place for filling
	 \return nothing
*/
	void UpdateContactL(TContactInfo& iInfo);
/**
	Fill contacts info array
	 \param CListContactsArray* aArray - place for filling, memory must be allocate before call
	 \return nothing
*/
	void UpadteContactsL(CListContactsArray* aArray);

/*!
  @function NewL
   
  @discussion Create a CPhoneBookTools object
  @result a pointer to the created instance of CPhoneBookTools
  */
    static CPhoneBookTools* NewL();

/*!
  @function NewLC
   
  @discussion Create a CPhoneBookTools object
  @result a pointer to the created instance of CPhoneBookTools
  */
    static CPhoneBookTools* NewLC();

/*!
  @function ~CPhoneBookTools
  
  @discussion Destroy the object and release all memory objects
  */
    ~CPhoneBookTools();

/**
	 Seek pictuire path in CntDb and update phone
	 \param const TDesC& aPath - picture path
	 \param CListContactsArray& cntArray - array for founded results
	 \return nothing
*/
	void GetPhoneByPathL(const TDesC& aPath,CListContactsArray& cntArray);


private:
/**
	 Load thumb :)
*/
	TBool LoadThumb();
/*!
  @function CPhoneBookTools
  @discussion Constructs this object
  */
    CPhoneBookTools();
/*!
  @function ConstructL
  @discussion Performs second phase construction of this object
  */
    void ConstructL();

private:
	CContactDatabase* iDB; /**< Opened CntDb handle */

};

#endif // __PHONEBOOKTOOLS_H__

