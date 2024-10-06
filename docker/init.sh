#!/bin/bash

# Redis 노드가 시작되고 서비스가 완전히 준비되기를 기다린다.
sleep 1

redis-cli --cluster call redis-master-1:7001 flushall
redis-cli --cluster call redis-master-1:7001 cluster reset

# 마스터 설정하기
echo "yes" | redis-cli --cluster create redis-master-1:7001 redis-master-2:7002 redis-master-3:7003

# 슬레이브 등록하기
echo "yes" | redis-cli --cluster add-node redis-replica-1:7101 redis-master-1:7001 --cluster-slave
echo "yes" | redis-cli --cluster add-node redis-replica-2:7102 redis-master-2:7002 --cluster-slave
echo "yes" | redis-cli --cluster add-node redis-replica-3:7103 redis-master-3:7003 --cluster-slave

# 노드만 만들고 클러스터 구성 안 한 상태로 Master-slave 한번에 구성하기
#echo "yes" | redis-cli --cluster create redis-master-1:7001 redis-master-2:7002 redis-master-3:7003 redis-replica-1:7101 redis-replica-2:7102 redis-replica-3:7103 --cluster-replicas 1

# 클러스터 정보 확인
redis-cli --cluster check redis-master-1:7001

# redis-stat
#java -jar redis-stat-0.4.14.jar localhost:7001 localhost:7002 localhost:7003 localhost:7101 localhost:7102 localhost:7103 --server=8888