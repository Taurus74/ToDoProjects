package com.tausoft.todoprojects.exchange

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class PublishedBoolean(private var aBoolean: Boolean?) {
    private val changeObservable = PublishSubject.create<Boolean>()

    fun setBoolean(aBoolean: Boolean) {
        this.aBoolean = aBoolean
        changeObservable.onNext(aBoolean)
    }

    fun getChanges(): Observable<Boolean> {
        return changeObservable
    }
}