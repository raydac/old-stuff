/* ========================================================================
  * Author:  Oleg Golosovskiy, http://www.sw4u.org mailto:mail@sw4u.org
  * ======================================================================== */
/** 
  * \author Oleg Golosovsky     
  */

#include "PhoneBookTools.h"
#include "SmViewerContainer3.h"
#include <e32std.h>
#include <cntfield.h>
#include <cntitem.h>
#include <cntfldst.h>
#include <GULICON.H>


CPhoneBookTools* CPhoneBookTools::NewL()
    {
    CPhoneBookTools* self = NewLC();
    CleanupStack::Pop(self);
    return self;
    }

    
CPhoneBookTools* CPhoneBookTools::NewLC()
    {
    CPhoneBookTools* self = new (ELeave) CPhoneBookTools();
    CleanupStack::PushL(self);
    self->ConstructL();
    return self;
    }


CPhoneBookTools::CPhoneBookTools() : iDB(0) 
{
}


CPhoneBookTools::~CPhoneBookTools()
{


	delete iDB;
}


void CPhoneBookTools::ConstructL()
{
	iDB = CContactDatabase::OpenL(); 	
}

 
	 

void CPhoneBookTools::GetPhoneByPathL(const TDesC& aPath,CListContactsArray& cntArray)
{
	if(!iDB)
		return;
//	__UHEAP_MARK;


	cntArray.Reset();

	CContactItemFieldDef *def = new (ELeave) CContactItemFieldDef;
	CleanupStack::PushL(def);
	def->AppendL(KUidContactFieldVCardMapUnknown);
	const CContactIdArray* items2 = iDB->FindLC(aPath, def); // will hang

	for (TInt i=0;i<items2->Count();i++)
	{
		TContactInfo info;
		info.iFileName.Set(aPath,0,0);
		const TContactItemId& id = (*items2)[i] ;
		CContactItem* item = iDB->ReadContactLC( id ) ;
		CContactItemFieldSet& fields = item->CardFields() ;

		TInt pos=fields.Find(KUidContactFieldPhoneNumber);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iTelephone.Append(text->Text());
			}
		}


		info.iContactName = _L("");
		pos=fields.Find(KUidContactFieldGivenName);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iContactName.Append(text->Text());
			}
		}

		info.iContactName.Append(_L(" "));
		pos=fields.Find(KUidContactFieldFamilyName);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iContactName.Append(text->Text());
			}
		}

		info.iContactName.Append(_L(" "));
		pos=fields.Find(KUidContactFieldAdditionalName);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iContactName.Append(text->Text());
			}
		}

		iDB->CloseContactL(id);
		CleanupStack::PopAndDestroy(item);// item CloseContactL

		cntArray.AppendL(info);
	}

	CleanupStack::PopAndDestroy(2,def);

//	__UHEAP_MARKEND;

}


void CPhoneBookTools::UpadteContactsL(CListContactsArray *aArray)
{
	if(!iDB)
		return;
//	__UHEAP_MARK;

	__ASSERT_ALWAYS(aArray!=0,User::Leave(KErrArgument));
	aArray->Reset();


	const CContactIdArray* items = iDB->SortedItemsL() ;
	for (TInt ind=0;ind<items->Count();ind++)
	{
		const TContactItemId& id = (*items)[ind] ;
		CContactItem* item = iDB->ReadContactLC( id ) ;
		CContactItemFieldSet& fields = item->CardFields() ;
		TContactInfo info;

		TInt pos=fields.Find(KUidContactFieldPhoneNumber);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iTelephone = text->Text();
			}
		}


		info.iContactName = _L("");
		
		pos=fields.Find(KUidContactFieldFamilyName);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iContactName.Append(text->Text());
			}
		}

		if(info.iContactName.Length()>0)
			info.iContactName.Append(_L(" "));
		pos=fields.Find(KUidContactFieldGivenName);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iContactName.Append(text->Text());
			}
		}

/*
		if(info.iContactName.Length()>0)
			info.iContactName.Append(_L(" "));
		pos=fields.Find(KUidContactFieldAdditionalName);
		if (pos>=0 && pos< fields.Count() ) 
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				info.iContactName.Append(text->Text());
			}
		}
*/

		pos = 0;
		while(-1!=(pos=fields.FindNext(KUidContactFieldPicture,KUidContactFieldVCardMapUnknown,pos)))
		{
			const CContactItemField& field1 = fields[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				CContactTextField* text = field1.TextStorage();
				TFileName fname = text->Text();
				fname.LowerCase();
				info.iFileName.Set(fname,0,0);
				info.iLinked = ETrue;
				break;
			}
		}


		for ( TInt j = 0 ; j < fields.Count() ; j++ )
		{
			const CContactItemField& field2 = fields[j] ;
			TStorageType storageType = field2.StorageType() ;
			const CContentType& contentType = field2.ContentType() ;
			if ( storageType == KStorageTypeStore && contentType.Mapping() == KUidContactFieldVCardMapPHOTO )
			{

				CContactStoreField* storage = field2.StoreStorage() ;
				HBufC8* buf = storage->Thing();
				if(buf->Length()>0)
				{
					HBufC8* newbuf = HBufC8::NewL(buf->Length()+1);
					(*newbuf) = (*buf);
					info.iThumbail = newbuf;
				}
				break;
			}
		}

		iDB->CloseContactL(id);
		CleanupStack::PopAndDestroy(item);// item CloseContactL

		if(info.iTelephone.Length()!=0)
			aArray->AppendL(info);
		else
		{
			delete info.iThumbail;
			info.iThumbail=0;
		}

	}


//	__UHEAP_MARKEND;	
}

void CPhoneBookTools::UpdateContactL(TContactInfo &iInfo)
{
	if(!iDB)
		return;


	CContactItemFieldDef *def = new (ELeave) CContactItemFieldDef;
	CleanupStack::PushL(def);
	def->AppendL(KUidContactFieldPhoneNumber);
	const CContactIdArray* items2 = iDB->FindLC(iInfo.iTelephone, def); // will hang

	if (items2->Count()>0)
	{
		const TContactItemId& id = (*items2)[0] ;
		CContactItem* item = iDB->OpenContactL( id ) ;
		CleanupStack::PushL(item);


		CContactItemFieldSet& fieldSet=item->CardFields();


// update старое поле с большой картинкой
		TInt pos = 0;
		while(-1!=(pos=fieldSet.FindNext(KUidContactFieldPicture,KUidContactFieldVCardMapUnknown,pos)))
		{
			const CContactItemField& field1 = fieldSet[pos] ;
			TStorageType storageType = field1.StorageType() ;
			if (storageType == KStorageTypeText)
			{
				if(iInfo.iLinked!=EFalse)
					fieldSet[pos].TextStorage()->SetTextL(iInfo.iFileName.FullName());
				else
					fieldSet.Remove(pos);
				break;
			}
		}

// добавить новое поле с большой картинкой
		if((pos==-1)&&(iInfo.iLinked!=EFalse)&&(iInfo.iFileName.FullName().Length()!=0))
		// add new field
		{
			TFieldType type;
			type.iUid = KUidContactFieldPictureValue;
			CContactItemField* field=CContactItemField::NewLC(KStorageTypeText,type);
			field->SetMapping(KUidContactFieldVCardMapUnknown);
			field->TextStorage()->SetTextL(iInfo.iFileName.FullName());
			item->AddFieldL(*field);
			CleanupStack::Pop(); // item
		}

// удалить поле c маленькой картинкой
		if((iInfo.iThumbail==0))
		{
			for ( TInt j = 0 ; j < fieldSet.Count() ; j++ )
			{
				const CContactItemField& field2 = fieldSet[j] ;
				TStorageType storageType = field2.StorageType() ;
				const CContentType& contentType = field2.ContentType() ;
//#ifdef _DEBUG
//				for(TInt j=0;j<contentType.FieldTypeCount();j++)
//					TFieldType tp = contentType.FieldType(j);
//#endif
				if ( storageType == KStorageTypeStore && contentType.Mapping() == KUidContactFieldVCardMapPHOTO )
				{
					fieldSet.Remove(j);
					break;
				}
			}

		}
		else
// добавить поле c маленькой картинкой
		{
			for ( TInt j = 0 ; j < fieldSet.Count() ; j++ )
			{
				const CContactItemField& field2 = fieldSet[j] ;
				TStorageType storageType = field2.StorageType() ;
				const CContentType& contentType = field2.ContentType() ;
				if ( storageType == KStorageTypeStore && contentType.Mapping() == KUidContactFieldVCardMapPHOTO )
				{
					fieldSet.Remove(j);
					break;
				}
			}

			TFieldType type;
			type.iUid = KUidContactFieldPictureValue;
			CContactItemField* field=CContactItemField::NewLC(KStorageTypeStore,type);
			field->SetMapping(KUidContactFieldVCardMapPHOTO);
			field->StoreStorage()->SetThingL(iInfo.iThumbail->Des());				
			item->AddFieldL(*field);
			CleanupStack::Pop(); // item			
			delete iInfo.iThumbail;
			iInfo.iThumbail=0;

		}

		iDB->CommitContactL(*item);
		iDB->CloseContactL(id);
		CleanupStack::PopAndDestroy(item);// item CloseContactL
	}

	CleanupStack::PopAndDestroy(2,def);

}

