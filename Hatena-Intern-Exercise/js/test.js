'use strict';
const assert = chai.assert;

/**
 * 課題 JS-1
 */
describe('parseLTSVLog', () => {
    it('関数定義の確認', () => {
        assert(typeof parseLTSVLog === 'function', '`parseLTSVLog` という名前の関数がある');
    });

    it('1行のみのログデータをパースできる', () => {
        const logStr = 'path:/\tepoch:500000\n';
        const logRecords = parseLTSVLog(logStr);
        assert.deepEqual(logRecords, [
            { path: '/', epoch: 500000 }
        ], '1行のみのログデータが期待通りパースされる');
    });

    it('3行からなるログデータをパースできる', () => {
        const logStr =
            'path:/\tepoch:400000\n' +
            'path:/uname\tepoch:123456\n' +
            'path:/\tepoch:500000\n';
        const logRecords = parseLTSVLog(logStr);
        assert.deepEqual(logRecords, [
            { path: '/',      epoch: 400000 },
            { path: '/uname', epoch: 123456 },
            { path: '/',      epoch: 500000 }
        ], '3 行からなるログデータが期待通りパースされる');
    });

    it('空文字列を渡したときは空の配列を返す', () => {
        const logStr = '';
        const logRecords = parseLTSVLog(logStr);
        assert.deepEqual(logRecords, [], '空文字列を渡したときは空の配列を返す');
    });

    // テストを追加する場合は `it` 関数や `describe` 関数を用い、この下に追加してください。
});

/**
 * 課題 JS-2
 */
describe('createLogTable', () => {
    const logs = [
        { path: '/', epoch: 400000 },
        { path: '/uname', epoch: 123456 },
        { path: '/', epoch: 500000 },
    ];

    const fixtureElem = document.createElement('div');

    before(() => document.body.appendChild(fixtureElem));
    after(() => document.body.removeChild(fixtureElem));

    afterEach(() => {
        fixtureElem.innerHTML = '';
    });

    it('関数定義の確認', () => {
        assert(typeof createLogTable === 'function', '`createLogTable` という名前の関数がある');
    });

    it('table要素を正しく出力する', () => {
        const elem = fixtureElem.appendChild(document.createElement('div'));
        createLogTable(elem, logs);

        assert(elem.childNodes.length === 1, '渡した要素に子ノードが 1 つ追加されている');

        const tableElem = elem.firstChild;
        assert(tableElem.tagName === 'TABLE', '渡した要素に追加された子ノードは table 要素');
        assert(tableElem.childNodes.length === 2, 'table 要素の子ノードは 2 個');

        const [theadElem, tbodyElem] = tableElem.childNodes;
        assert(theadElem.tagName === 'THEAD', 'table 要素の 1 つ目の子ノードは thead 要素');
        assert(theadElem.childNodes.length === 1, 'thead 要素の子ノードは 1 個');
        assert(tbodyElem.tagName === 'TBODY', 'table 要素の 2 つ目の子ノードは tbody 要素');
        assert(tbodyElem.childNodes.length === 3, 'tbody 要素の子ノードは 3 個');

        const ths = Array.from(elem.querySelectorAll('thead tr th'));
        const headers = ths.map(th => th.innerHTML);
        assert.deepEqual(headers, ['path', 'epoch'], 'thead 要素の子要素の tr 要素の中身が正しい');
    });

    it('ログの内容を正しく出力する', () => {
        const elem = fixtureElem.appendChild(document.createElement('div'));
        createLogTable(elem, logs);

        const trs = Array.from(elem.querySelectorAll('tbody tr'));
        trs.forEach((tr, i) => {
            const actual   = Array.from(tr.querySelectorAll('td')).map(td => td.innerHTML);
            const expected = [logs[i].path, logs[i].epoch.toString()];
            assert.deepEqual(actual, expected, `tbody 要素の子要素の ${i} 番目の tr 要素の中身が正しい`);
        });
    });

    // テストを追加する場合は `it` 関数や `describe` 関数を用い、この下に追加してください。
});
