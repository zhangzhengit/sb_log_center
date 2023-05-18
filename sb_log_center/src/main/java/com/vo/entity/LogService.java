package com.vo.entity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.CacheMode;
import org.hibernate.SessionFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.votool.ze.ZE;
import com.votool.ze.ZERunnable;
import com.votool.ze.ZES;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年11月24日
 *
 */
@Service
public class LogService implements InitializingBean {


	private final ZE ZE = ZES.newSingleZE();

	private FullTextEntityManager fullTextEntityManager;

	@Autowired
	private LogRepository logRepository;
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@PersistenceContext
	private EntityManager entityManager;

	public List<LogEntity> search(final String key, final Integer app) {



		final QueryBuilder qb = this.fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(LogEntity.class).get();

		final org.apache.lucene.search.Query luceneQuery = qb.keyword()
				.fuzzy()
				.withEditDistanceUpTo(1)
				.withPrefixLength(1)
				.onFields("content", "appName")
//				.onFields("content", "appName", "appId")
				.matching(key)
				.createQuery();

		// FIXME 2022年12月17日 上午12:49:56 zhanghen: 怎么匹配appId 字段 = app参数?

		final javax.persistence.Query jpaQuery = this.fullTextEntityManager.createFullTextQuery(luceneQuery,
				LogEntity.class);

		jpaQuery.setFirstResult(0);
		jpaQuery.setMaxResults(200);

		try {
			final List<LogEntity> r = jpaQuery.getResultList();
			return r;
		} catch (final NoResultException nre) {
			nre.printStackTrace();
		}

		return Collections.emptyList();
	}

	@Transactional(rollbackOn = Exception.class)
	public void saveAll_q20(final List<LogEntity> list) {
		for (final LogEntity logEntity : list) {
			this.logRepository.save(logEntity);
//			entityManager.creaIn
//			this.fullTextEntityManager.createIndexer(LogEntity.class).start();
//			this.logRepository
		}

//		final EntityManager em = this.entityManagerFactory.createEntityManager();
//
//		this.fullTextEntityManager.flush();
	}

	@Transactional(rollbackOn = Exception.class)
	public void saveAll_0(final List<LogEntity> list) {

		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "LogService.saveAll_0()开始.list.size = " + list.size());

		final String insert = "INSERT INTO log (content, app_id, app_name,create_time) VALUES ";

		final StringBuilder builder = new StringBuilder(insert);

		for (int i = 0; i < list.size(); i++) {

			final LogEntity eee = list.get(i);

			builder.append("('").append(eee.getContent()).append("',");
			builder.append("'").append(eee.getAppId()).append("',");
			builder.append("'").append(eee.getAppName()).append("',");
			builder.append(" CURRENT_TIMESTAMP),");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(';');

		final Query createNativeQuery = this.entityManager.createNativeQuery(builder.toString());
		final int executeUpdate = createNativeQuery.executeUpdate();
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "LogService.saveAll_0().list.size = " + list.size());

	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
	public void afterPropertiesSet() throws Exception {

		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "LogService.afterPropertiesSet()");

		this.ZE.executeInQueue(new ZERunnable<String>() {

			@Override
			public void run() {
				System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
						+ "LogService.afterPropertiesSet().new ZERunnable() {...}.run()");

				LogService.this.entityManagerFactory.unwrap(SessionFactory.class).openSession();

				final EntityManager entityManager = LogService.this.entityManagerFactory.createEntityManager();

				LogService.this.fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
				LogService.this.fullTextEntityManager.setProperty(null, entityManager);

				LogService.this.initIndex();

			}
		});

	}

	private void initIndex() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "ArticleService.initIndex()");

		final int batchSize = 1000;

		final Future<?> future = this.fullTextEntityManager.createIndexer()
				.batchSizeToLoadObjects(batchSize)
				.purgeAllOnStart(true)
				.optimizeAfterPurge(true)
				.cacheMode(CacheMode.NORMAL)
				.threadsToLoadObjects(Runtime.getRuntime().availableProcessors())
				.typesToIndexInParallel(Runtime.getRuntime().availableProcessors())
				.start();

		LogService.indexDone.set(future.isDone());

//		fullTextEntityManager.createEntityGraph(null)

		try {
			final Object object = future.get();
			LogService.indexDone.set(true);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}


	}

	public static AtomicBoolean indexDone = new AtomicBoolean(false);

}
