package com.example.uploadingfiles.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.uploadingfiles.database.SlidesToScript;
import com.example.uploadingfiles.database.User;


@Repository
public interface SlidesRepo extends CrudRepository<SlidesToScript, Long>{
	int countByUser(User user);
}
