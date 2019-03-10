import ProcessingQueue from '...';

describe('ProcessingQueue', function () {
    it('must pass shadow test', function () {
        const q = new ProcessingQueue<number>();
        q.add('one', 1);
        expect(q.poll()).toBe(1);
    });
    describe('.size', function () {
        it('should get correct size for empty', function () {
            const q = new ProcessingQueue<number>();
            expect(q.size).toBe(0);
        });
        it('should get correct size for filled', function () {
            const q = new ProcessingQueue<number>();
            q.add('one', 1);
            expect(q.size).toBe(1);
            q.add('two', 2);
            expect(q.size).toBe(2);
            q.poll();
            expect(q.size).toBe(1);
        });
    });
    describe('.add', function () {
        it('should add any number of elements', function () {
            const q = new ProcessingQueue<number>();
            q.add('one', 1);
            q.add('two', 2);
            q.add('three', 3);
            expect(q.size).toBe(3);
        });
        it('should NOT add existing key', function () {
            const q = new ProcessingQueue<number>();
            q.add('one', 1);
            q.add('one', 1);
            q.add('one', 1);
            expect(q.size).toBe(1);
            q.add('two', 2);
            q.add('one', 1);
            expect(q.size).toBe(2);
        });
        it('should not add already processed key', function () {
            const q = new ProcessingQueue<number>();
            q.add('one', 1);
            expect(q.size).toBe(1);
            q.add('two', 2);
            expect(q.size).toBe(2);
            expect(q.poll()).toBe(1);
            expect(q.size).toBe(1);
            expect(q.poll()).toBe(2);
            q.add('one', 1);
            q.add('two', 2);
            expect(q.poll()).toBe(undefined);
            expect(q.size).toBe(0);
        });
    });
    describe('.contains', function () {
        it('should find present key', function () {
            const q = new ProcessingQueue<number>();
            q.add('one', 1);
            expect(q.contains('one')).toBe(true);
        });
        it('should NOT find non-present key', function () {
            const q = new ProcessingQueue<number>();
            q.add('one', 1);
            expect(q.contains('invalid')).toBe(false);
        });
    });
    describe('.poll', function () {
        it('should return undefined for empty queue', function () {
            const q = new ProcessingQueue<number>();
            expect(q.poll()).toBe(undefined);
        });
        it('should return the top element for non-empty queue', function () {
            const q = new ProcessingQueue<number>();
            q.add('one', 1);
            expect(q.poll()).toBe(1);
            expect(q.poll()).toBe(undefined);
        });
    });
});
