package com.spring.orm.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.orm.hibernate5.HibernateTemplate;

import com.spring.orm.entities.Emplo;

public class Emplodao {
	
	private HibernateTemplate hibTemp;
	
//	Save Employee 
	@Transactional
	public int insert(Emplo emp)
	{
		Integer i = (Integer) this.hibTemp.save(emp);
		return i;
	}
	
//	single data show
	public Emplo emplo(int id)
	{
		Emplo em = this.hibTemp.get(Emplo.class, id);
		return em;
	}
	
//	Get all Data show
	public List<Emplo> getAlldata()
	{
		List<Emplo> all = this.hibTemp.loadAll(Emplo.class); 
		return all;
	}
	
//	Deleting data
	@Transactional
	public void deleteEmp(int id)
	{
		Emplo empo = this.hibTemp.get(Emplo.class, id);	
		this.hibTemp.delete(empo);
	}
	
//	Updating Data
	@Transactional
	public void updateEmp(Emplo emp)
	{
		this.hibTemp.update(emp);
	}


	public HibernateTemplate getHibTemp() {
		return hibTemp;
	}

	public void setHibTemp(HibernateTemplate hibTemp) {
		this.hibTemp = hibTemp;
	}
	
	
}
