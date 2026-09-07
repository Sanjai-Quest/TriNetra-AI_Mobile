import React from 'react';
import { ClaimDetail } from '../services/api';
import { Activity, AlertTriangle, Camera, Mic, Package, Scale } from 'lucide-react';

interface ClaimDetailViewProps {
  claim: ClaimDetail | null;
  onOpenOverride: () => void;
}

export const ClaimDetailView: React.FC<ClaimDetailViewProps> = ({ claim, onOpenOverride }) => {
  if (!claim) {
    return (
      <div className="bg-slate-900/40 border border-slate-800/80 rounded-2xl p-12 text-center text-slate-500 h-full flex flex-col items-center justify-center space-y-3">
        <Package className="w-12 h-12 text-slate-600 animate-bounce" />
        <h3 className="text-sm font-semibold text-slate-400">Select a Claim to Inspect</h3>
        <p className="text-xs text-slate-500 max-w-xs">
          Inspect multi-modal artifacts, detected fraud patterns, and submit investigator override decisions.
        </p>
      </div>
    );
  }

  const getSeverityStyle = (severity: string) => {
    switch (severity.toLowerCase()) {
      case 'critical':
        return 'bg-rose-500/10 border-rose-500/30 text-rose-300';
      case 'high':
        return 'bg-orange-500/10 border-orange-500/30 text-orange-300';
      case 'medium':
        return 'bg-amber-500/10 border-amber-500/30 text-amber-300';
      default:
        return 'bg-emerald-500/10 border-emerald-500/30 text-emerald-300';
    }
  };

  return (
    <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-6 flex flex-col h-full overflow-y-auto space-y-6">
      <header className="border-b border-slate-800 pb-4 flex items-start justify-between">
        <div>
          <span className="text-[11px] font-mono text-indigo-400 font-bold uppercase">Claim Detail</span>
          <h2 className="text-lg font-bold text-white font-mono">{claim.orderId}</h2>
          <p className="text-xs text-slate-400">Case {claim.caseId || claim.claimId} · {claim.product || 'Smartphone'}</p>
        </div>
        <button onClick={onOpenOverride} className="text-xs px-3 py-2 rounded-lg bg-amber-500/15 text-amber-300 border border-amber-500/30">
          Investigator decision
        </button>
      </header>

      <section className="grid grid-cols-2 gap-3">
        <Metric label="Outbound weight" value={`${claim.outboundWeightGrams ?? 0} g`} />
        <Metric label="Return weight" value={`${claim.returnWeightGrams ?? 0} g`} />
        <Metric label="Difference" value={`${(claim.differencePercent ?? 0).toFixed(1)}%`} />
        <Metric label="Threshold" value="5.0%" />
      </section>

      <section className="rounded-xl border border-amber-500/30 bg-amber-500/10 p-4">
        <div className="flex items-center gap-2 text-amber-300 font-bold"><AlertTriangle className="w-4 h-4" /> {claim.state || 'INVESTIGATE'}</div>
        <p className="mt-2 text-sm text-slate-300">{claim.reasoning || 'Deterministic reconciliation is pending.'}</p>
      </section>

      <section className="space-y-3">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">Evidence sources</h3>
        {claim.evidence?.map(item => (
          <div key={item.evidenceId} className="rounded-xl border border-slate-800 bg-slate-950/70 p-3">
            <div className="flex items-center gap-2 text-sm text-white"><SourceIcon source={item.source} /> {item.source}</div>
            <pre className="mt-2 text-[11px] text-slate-400 whitespace-pre-wrap">{item.payloadJson}</pre>
            <div className="mt-2 text-[10px] text-slate-500">{new Date(item.createdAt).toLocaleString()}</div>
          </div>
        ))}
      </section>
    </div>
  );
};

const Metric = ({ label, value }: { label: string; value: string }) => (
  <div className="rounded-xl bg-slate-950/70 border border-slate-800 p-3">
    <span className="block text-[10px] uppercase font-bold text-slate-500">{label}</span>
    <span className="block mt-1 text-lg font-bold text-white">{value}</span>
  </div>
);

const SourceIcon = ({ source }: { source: string }) => {
  if (source.includes('SCALE')) return <Scale className="w-4 h-4 text-cyan-300" />;
  if (source.includes('VOICE')) return <Mic className="w-4 h-4 text-violet-300" />;
  if (source.includes('CAMERA') || source.includes('CV')) return <Camera className="w-4 h-4 text-emerald-300" />;
  return <Activity className="w-4 h-4 text-slate-300" />;
};
